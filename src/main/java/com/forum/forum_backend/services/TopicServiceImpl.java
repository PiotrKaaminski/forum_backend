package com.forum.forum_backend.services;

import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.dtos.CommentDto;
import com.forum.forum_backend.dtos.TopicDto;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.TopicEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.repositories.TopicRepository;
import com.forum.forum_backend.repositories.UserRepository;
import com.forum.forum_backend.services.interfaces.TopicService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Transactional
@Service
public class TopicServiceImpl implements TopicService {
	
	private final TopicRepository topicRepository;
	private final UserRepository userRepository;
	private final UserService userService;

	public TopicServiceImpl(TopicRepository topicRepository, UserRepository userRepository, UserService userService) {
		this.topicRepository = topicRepository;
		this.userRepository = userRepository;
		this.userService = userService;
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
			topic.setLikesAmount(topicEntity.getUsersLikes().size());
			topic.setCommentsAmount(null);

			topic.setComments(new ArrayList<>() {{
				addAll(topicEntity.getComments().stream().map(x -> new CommentDto() {{
					setId(x.getId());
					setContent(x.getContent());

					UserDto commentAuthor = new UserDto();
					commentAuthor.setId(x.getUser().getId());
					commentAuthor.setUsername(x.getUser().getUsername());

					setCommentAuthor(commentAuthor);
					setLikesAmount(x.getUsersLikes().size());
				}}).collect(Collectors.toList()));
			}});

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
	public void addLike(int topicId) throws NotFoundException {
		try {
			TopicEntity topic = topicRepository.getOne(topicId);

			UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			UserEntity user = userService.getUserById(userPrincipal.getId());

			if(user.getLikedTopics().contains(topic)){
				user.getLikedTopics().remove(topic);
			} else {
				user.addTopicLike(topic);
				userRepository.save(user);
			}

		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Topic with id = " + topicId + " doesn't exist");
		}
	}

	@Override
	public void modifyTopic(TopicDto topicDto, int topicId) throws UnauthorizedException, NotFoundException {

		try {
			TopicEntity topic = topicRepository.getOne(topicId);

			if (userService.isUserAuthorized(topic.getUser())) {
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

			if (userService.isUserAuthorized(topic.getUser())) {
				topicRepository.delete(topic);
			} else {
				throw new UnauthorizedException("You have no permissions to delete this topic");
			}
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Topic with id = " + topicId + " doesn't exist");
		}
	}

	@Override
	public TopicDto mapChildEntityToDto(TopicEntity topicEntity) {
		return new TopicDto() {{
			setId(topicEntity.getId());
			setHeader(topicEntity.getHeader());

			UserDto author = new UserDto();
			author.setId(topicEntity.getUser().getId());
			author.setUsername(topicEntity.getUser().getUsername());
			setTopicAuthor(author);

			setCommentsAmount(topicEntity.getComments().size());
			setLikesAmount(topicEntity.getUsersLikes().size());
		}};
	}
}
