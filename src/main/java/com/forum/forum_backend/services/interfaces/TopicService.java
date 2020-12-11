package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.TopicDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;

import java.util.List;

public interface TopicService {

	List<TopicDto> getTopicList();
	TopicDto getTopic(int topicId) throws NotFoundException;
	void addTopic(TopicDto topicDto);
	void addLike(int topicId) throws NotFoundException;
	void modifyTopic(TopicDto topicDto, int topicId) throws UnauthorizedException, NotFoundException;
	void deleteTopic(int topicId) throws UnauthorizedException, NotFoundException;
}
