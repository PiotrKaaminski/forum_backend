package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.models.TopicEntity;

import java.util.List;

public interface TopicService {

	List<TopicEntity> getTopicList();
}
