package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.PostDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.services.interfaces.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/posts")
public class PostController {

	private final PostService postService;

	public PostController(PostService postService) {
		this.postService = postService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void addPost(@RequestParam(name = "threadId") int threadId, @Valid @RequestBody PostDto postDto)
			throws NotFoundException {
		postService.addPost(threadId, postDto);
	}

	@PostMapping("/{postId}")
	@ResponseStatus(HttpStatus.CREATED)
	public void addLike(@PathVariable int postId)
			throws NotFoundException {
		postService.addLike(postId);
	}

	@PutMapping("/{postId}")
	public void modifyPost(@PathVariable int postId ,@Valid @RequestBody PostDto postDto)
			throws UnauthorizedException, NotFoundException {
		postService.modifyPost(postId, postDto);
	}

	@DeleteMapping("/{postId}")
	public void deletePost(@PathVariable int postId)
			throws UnauthorizedException, NotFoundException {
		postService.deletePost(postId);
	}


}
