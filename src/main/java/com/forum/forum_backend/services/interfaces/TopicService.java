package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.TopicDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.ThreadEntity;

public interface TopicService {

	TopicDto getTopic(int topicId) throws NotFoundException;
	void addTopic(TopicDto topicDto);
	void addLike(int topicId) throws NotFoundException;
	void modifyTopic(TopicDto topicDto, int topicId) throws UnauthorizedException, NotFoundException;
	void deleteTopic(int topicId) throws UnauthorizedException, NotFoundException;
	TopicDto mapChildEntityToDto(ThreadEntity threadEntity);
}
