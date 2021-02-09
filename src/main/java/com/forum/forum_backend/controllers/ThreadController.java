package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.ThreadDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.services.interfaces.ThreadService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/threads")
public class ThreadController {

	private final ThreadService threadService;

	public ThreadController(ThreadService threadService) {
		this.threadService = threadService;
	}

	@GetMapping("/{threadId}")
	@ResponseStatus(HttpStatus.OK)
	public ThreadDto getThread(@PathVariable int threadId)
			throws NotFoundException {
		return threadService.getThread(threadId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ThreadDto addThread(@Valid @RequestBody ThreadDto threadDto)
			throws NotFoundException {
		return threadService.addThread(threadDto);
	}

	@PostMapping("/{threadId}")
	@ResponseStatus(HttpStatus.CREATED)
	public void addLike(@PathVariable int threadId)
		throws NotFoundException {
		threadService.addLike(threadId);
	}

	@PutMapping("/{threadId}")
	public void modifyThread(@Valid @RequestBody ThreadDto threadDto, @PathVariable int threadId)
			throws UnauthorizedException, NotFoundException {
		threadService.modifyThread(threadDto, threadId);
	}

	@DeleteMapping("/{threadId}")
	public void deleteTopic(@PathVariable int threadId)
			throws UnauthorizedException, NotFoundException {
		threadService.deleteThread(threadId);
	}

}
