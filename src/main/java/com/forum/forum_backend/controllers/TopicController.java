package com.forum.forum_backend.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.forum.forum_backend.Views.TopicView;
import com.forum.forum_backend.dtos.TopicDto;
import com.forum.forum_backend.services.interfaces.TopicService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

	private final TopicService topicService;

	public TopicController(TopicService topicService) {
		this.topicService = topicService;
	}

	@GetMapping
	@JsonView(TopicView.Minimal.class)
	public List<TopicDto> getTopicList() {
		return topicService.getTopicList();
	}

	@GetMapping("/{topicId}")
	@JsonView(TopicView.Extended.class)
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
