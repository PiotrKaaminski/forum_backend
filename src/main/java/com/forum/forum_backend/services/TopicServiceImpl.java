package com.forum.forum_backend.services;

import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.dtos.TopicDto;
import com.forum.forum_backend.models.TopicEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.repositories.TopicRepository;
import com.forum.forum_backend.services.interfaces.TopicService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class TopicServiceImpl implements TopicService {

	@Autowired
	private TopicRepository topicRepository;
	@Autowired
	private UserService userService;


	@Override
	public List<TopicEntity> getTopicList() {
		return topicRepository.findAll();
	}

	@Override
	public void addTopic(TopicDto topicDto) {
		System.out.println("TopicService->addTopic @@@ topicDto.header: " + topicDto.getHeader());
		System.out.println("TopicService->addTopic @@@ topicDto.content: " + topicDto.getContent());

		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = user.getId();
		UserEntity owner = userService.getUserById(userId);

		TopicEntity topic = new TopicEntity(topicDto.getHeader(), topicDto.getContent(), owner);
		topicRepository.save(topic);

	}

	@Override
	public void modifyTopic(TopicDto topicDto, int topicId) {
		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = user.getId();

		TopicEntity topic = topicRepository.getOne(topicId);

		if (topic.getUser().getId() == userId) {
			if(topicDto.getHeader() != null) {
				topic.setHeader(topicDto.getHeader());
			}
			if(topicDto.getContent() != null) {
				topic.setContent(topicDto.getContent());
			}
			topicRepository.save(topic);
		}
	}
}
