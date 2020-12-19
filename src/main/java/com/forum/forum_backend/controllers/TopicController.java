package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.TopicDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.services.interfaces.TopicService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

	private final TopicService topicService;

	public TopicController(TopicService topicService) {
		this.topicService = topicService;
	}

	@GetMapping("/{topicId}")
	@ResponseStatus(HttpStatus.OK)
	public TopicDto getTopic(@PathVariable int topicId)
			throws NotFoundException {
		return topicService.getTopic(topicId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void addTopic(@Valid @RequestBody TopicDto topicDto) {
		topicService.addTopic(topicDto);
	}

	@PostMapping("/{topicId}")
	@ResponseStatus(HttpStatus.CREATED)
	public void addLike(@PathVariable int topicId)
		throws NotFoundException {
		topicService.addLike(topicId);
	}

	@PutMapping("/{topicId}")
	public void modifyTopic(@Valid @RequestBody TopicDto topicDto, @PathVariable int topicId)
			throws UnauthorizedException, NotFoundException {
		topicService.modifyTopic(topicDto, topicId);
	}

	@DeleteMapping("/{topicId}")
	public void deleteTopic(@PathVariable int topicId)
			throws UnauthorizedException, NotFoundException {
		topicService.deleteTopic(topicId);
	}

}
