package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.TopicDto;
import com.forum.forum_backend.exceptions.UnauthorizedException;

import java.util.List;

public interface TopicService {

	List<TopicDto> getTopicList();
	TopicDto getTopic(int topicId);
	void addTopic(TopicDto topicDto);
	void modifyTopic(TopicDto topicDto, int topicId) throws UnauthorizedException;
	void deleteTopic(int topicId) throws UnauthorizedException;
}
