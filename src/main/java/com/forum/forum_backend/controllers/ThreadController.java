package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.ThreadDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.services.interfaces.ThreadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/threads")
public class ThreadController {

	private final ThreadService threadService;
	@Value("${pagination.defaultSize}")
	private int defaultPaginationSize;

	public ThreadController(ThreadService threadService) {
		this.threadService = threadService;
	}

	@GetMapping("/{threadId}")
	@ResponseStatus(HttpStatus.OK)
	public ThreadDto getThread(
			@PathVariable int threadId,
			@RequestParam(defaultValue = "0", required = false) int size,
			@RequestParam(defaultValue = "0", required = false) int page)
			throws NotFoundException {
		if (size == 0) {
			size = defaultPaginationSize;
		}
		return threadService.getThread(threadId, size, page);
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

	@DeleteMapping("/{threadId}")
	public void deleteTopic(@PathVariable int threadId)
			throws UnauthorizedException, NotFoundException {
		threadService.deleteThread(threadId);
	}

	@PatchMapping("/{threadId}")
	public void modifyThread(@PathVariable int threadId, @RequestBody ThreadDto threadDto)
			throws NotFoundException, UnauthorizedException {
		threadService.modifyThread(threadId, threadDto);
	}

}
