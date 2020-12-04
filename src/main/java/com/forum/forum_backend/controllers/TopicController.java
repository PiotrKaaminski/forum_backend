package com.forum.forum_backend.controllers;

import com.forum.forum_backend.models.CommentEntity;
import com.forum.forum_backend.models.TopicEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.services.interfaces.CommentService;
import com.forum.forum_backend.services.interfaces.TopicService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

	@Autowired
	private TopicService topicService;


	@GetMapping("/topics")
	public List<TopicEntity> getTopicList() {
		return topicService.getTopicList();
	}


}
