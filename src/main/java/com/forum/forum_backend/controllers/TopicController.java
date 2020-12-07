package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.TopicDto;
import com.forum.forum_backend.services.interfaces.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

	@Autowired
	private TopicService topicService;

	@GetMapping
	public List<TopicDto> getTopicList() {
		return topicService.getTopicList();
	}

	@GetMapping("/{topicId}")
	public TopicDto getTopic(@PathVariable int topicId) {
		return topicService.getTopic(topicId);
	}

	@PostMapping
	public void addTopic(@RequestBody TopicDto topicDto) {
		topicService.addTopic(topicDto);
	}

	@PutMapping("/{topicId}")
	public void modifyTopic(@RequestBody TopicDto topicDto, @PathVariable int topicId) {
		topicService.modifyTopic(topicDto, topicId);
	}

	@DeleteMapping("/{topicId}")
	public void deleteTopic(@PathVariable int topicId) {
		topicService.deleteTopic(topicId);
	}

}
