package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.TopicDto;
import com.forum.forum_backend.models.TopicEntity;

import java.util.List;

public interface TopicService {

	List<TopicEntity> getTopicList();
	TopicEntity getTopic(int topicId);
	void addTopic(TopicDto topicDto);
	void modifyTopic(TopicDto topicDto, int topicId);
}
