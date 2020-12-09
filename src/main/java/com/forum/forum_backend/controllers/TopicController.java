package com.forum.forum_backend.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.forum.forum_backend.dtos.TopicDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.services.interfaces.TopicService;
import com.forum.forum_backend.views.TopicView;
import org.springframework.http.HttpStatus;
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
	@ResponseStatus(HttpStatus.OK)
	public List<TopicDto> getTopicList() {
		return topicService.getTopicList();
	}

	@GetMapping("/{topicId}")
	@JsonView(TopicView.Extended.class)
	@ResponseStatus(HttpStatus.OK)
	public TopicDto getTopic(@PathVariable int topicId)
			throws NotFoundException {
		return topicService.getTopic(topicId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void addTopic(@RequestBody TopicDto topicDto) {
		topicService.addTopic(topicDto);
	}

	@PutMapping("/{topicId}")
	public void modifyTopic(@RequestBody TopicDto topicDto, @PathVariable int topicId)
			throws UnauthorizedException, NotFoundException {
		topicService.modifyTopic(topicDto, topicId);
	}

	@DeleteMapping("/{topicId}")
	public void deleteTopic(@PathVariable int topicId)
			throws UnauthorizedException, NotFoundException {
		topicService.deleteTopic(topicId);
	}

}
