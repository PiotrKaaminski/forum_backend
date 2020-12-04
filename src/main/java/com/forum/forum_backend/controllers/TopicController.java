package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.TopicDto;
import com.forum.forum_backend.models.CommentEntity;
import com.forum.forum_backend.models.TopicEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.services.interfaces.CommentService;
import com.forum.forum_backend.services.interfaces.TopicService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

	@Autowired
	private TopicService topicService;


	@GetMapping
	public List<TopicEntity> getTopicList() {
		return topicService.getTopicList();
	}

	@PostMapping
	public void addTopic(@RequestBody TopicDto topicDto) {
		topicService.addTopic(topicDto);
	}

	@PutMapping("/{topicId}")
	public void modifyTopic(@RequestBody TopicDto topicDto, @PathVariable int topicId) {
		topicService.modifyTopic(topicDto, topicId);
	}

}
