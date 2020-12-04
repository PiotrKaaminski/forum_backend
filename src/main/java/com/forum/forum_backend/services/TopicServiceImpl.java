package com.forum.forum_backend.services;

import com.forum.forum_backend.models.TopicEntity;
import com.forum.forum_backend.repositories.TopicRepository;
import com.forum.forum_backend.services.interfaces.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class TopicServiceImpl implements TopicService {

	@Autowired
	private TopicRepository topicRepository;


	@Override
	public List<TopicEntity> getTopicList() {
		return topicRepository.findAll();
	}
}
