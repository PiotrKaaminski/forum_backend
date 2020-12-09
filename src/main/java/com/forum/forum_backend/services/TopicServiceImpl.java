package com.forum.forum_backend.services;

import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.dtos.CommentDto;
import com.forum.forum_backend.dtos.TopicDto;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.CommentEntity;
import com.forum.forum_backend.models.TopicEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.repositories.TopicRepository;
import com.forum.forum_backend.services.interfaces.TopicService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class TopicServiceImpl implements TopicService {
	
	private final TopicRepository topicRepository;
	private final UserService userService;

	public TopicServiceImpl(TopicRepository topicRepository, UserService userService) {
		this.topicRepository = topicRepository;
		this.userService = userService;
	}

	@Override
	public List<TopicDto> getTopicList() {
		List<TopicEntity> topicEntities =  topicRepository.findAll();
		List<TopicDto> topics = new ArrayList<>();

		for (TopicEntity topic: topicEntities) {
			TopicDto tempTopic = new TopicDto();
			tempTopic.setId(topic.getId());
			tempTopic.setHeader(topic.getHeader());

			UserDto topicAuthor = new UserDto();
			topicAuthor.setId(topic.getUser().getId());
			topicAuthor.setUsername(topic.getUser().getUsername());
			tempTopic.setTopicAuthor(topicAuthor);
			topics.add(tempTopic);
		}
		return topics;
	}

	@Override
	public TopicDto getTopic(int topicId) throws NotFoundException {
		try {
			TopicEntity topicEntity = topicRepository.getOne(topicId);
			TopicDto topic = new TopicDto();
			topic.setId(topicEntity.getId());
			topic.setHeader(topicEntity.getHeader());
			topic.setContent(topicEntity.getContent());

			UserDto topicAuthor = new UserDto();
			topicAuthor.setId(topicEntity.getUser().getId());
			topicAuthor.setUsername(topicEntity.getUser().getUsername());
			topic.setTopicAuthor(topicAuthor);

			List<CommentDto> comments = new ArrayList<>();

			for (CommentEntity commentEntity : topicEntity.getComments()) {
				CommentDto tempComment = new CommentDto();
				tempComment.setId(commentEntity.getId());
				tempComment.setContent(commentEntity.getContent());

				UserDto commentAuthor = new UserDto();
				commentAuthor.setId(commentEntity.getUser().getId());
				commentAuthor.setUsername(commentEntity.getUser().getUsername());
				tempComment.setCommentAuthor(commentAuthor);
				comments.add(tempComment);
			}

			topic.setComments(comments);

			return topic;
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Topic with id = " + topicId + " doesn't exist");
		}
	}

	@Override
	public void addTopic(TopicDto topicDto) {
		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = user.getId();
		UserEntity owner = userService.getUserById(userId);

		TopicEntity topic = new TopicEntity(topicDto.getHeader(), topicDto.getContent(), owner);
		topicRepository.save(topic);
	}

	@Override
	public void modifyTopic(TopicDto topicDto, int topicId) throws UnauthorizedException, NotFoundException {

		try {
			TopicEntity topic = topicRepository.getOne(topicId);

			if (isUserAuthorized(topic)) {
				if (topicDto.getHeader() != null) {
					topic.setHeader(topicDto.getHeader());
				}
				if (topicDto.getContent() != null) {
					topic.setContent(topicDto.getContent());
				}
				topicRepository.save(topic);
			} else {
				throw new UnauthorizedException("You have no permissions to modify this topic");
			}
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Topic with id = " + topicId + " doesn't exist");
		}
	}

	@Override
	public void deleteTopic(int topicId) throws UnauthorizedException, NotFoundException {
		try {
			TopicEntity topic = topicRepository.getOne(topicId);

			if (isUserAuthorized(topic)) {
				topicRepository.delete(topic);
			} else {
				throw new UnauthorizedException("You have no permissions to delete this topic");
			}
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Topic with id = " + topicId + " doesn't exist");
		}
	}

	private boolean isUserAuthorized(TopicEntity topic) {
		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = user.getId();

		return topic.getUser().getId() == userId;
	}
}
