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

	@GetMapping("/{postId}")
	public PostDto getPost(@PathVariable int postId)
			throws NotFoundException {
		return postService.getPost(postId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PostDto addPost(@Valid @RequestBody PostDto postDto)
			throws NotFoundException {
		return postService.addPost(postDto.getThreadId(), postDto);
	}

	@PostMapping("/{postId}")
	@ResponseStatus(HttpStatus.CREATED)
	public void addLike(@PathVariable int postId)
			throws NotFoundException {
		postService.addLike(postId);
	}

	@PatchMapping("/{postId}")
	public PostDto modifyPost(@PathVariable int postId ,@Valid @RequestBody PostDto postDto)
			throws UnauthorizedException, NotFoundException {
		return postService.modifyPost(postId, postDto);
	}

	@DeleteMapping("/{postId}")
	public void deletePost(@PathVariable int postId)
			throws UnauthorizedException, NotFoundException {
		postService.deletePost(postId);
	}


}
