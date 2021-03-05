package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.ForumDto;
import com.forum.forum_backend.dtos.PaginatedResponse;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.services.interfaces.ForumService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/forums")
public class ForumController {

	private final ForumService forumService;

	public ForumController(ForumService forumService) {
		this.forumService = forumService;
	}

	@GetMapping
	public PaginatedResponse<ForumDto> getMainForumList() {
		return forumService.getMainForumList();
	}

	@GetMapping("/{forumId}")
	public ForumDto getSubForum(
			@PathVariable int forumId,
			@RequestParam(defaultValue = "true", required = false) boolean getThreads)
			throws NotFoundException {
		return forumService.getSubForum(forumId, getThreads);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ForumDto addForum(@Valid @RequestBody ForumDto forumDto) throws NotFoundException, UnauthorizedException {
		return forumService.addForum(forumDto);
	}


	@PatchMapping("/{forumId}")
	public ForumDto modifyForum(@Valid @RequestBody ForumDto forumDto, @PathVariable int forumId)
			throws UnauthorizedException, NotFoundException {
		return forumService.modifyForum(forumDto, forumId);
	}

	@DeleteMapping("/{forumId}")
	public void deleteForum(@PathVariable int forumId)
			throws UnauthorizedException, NotFoundException {
		forumService.deleteForum(forumId);
	}

}
