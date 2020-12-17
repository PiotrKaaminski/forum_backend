package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.CommentDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.services.interfaces.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

	private final CommentService commentService;

	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void addComment(@RequestParam(name = "topicId") int topicId, @Valid @RequestBody CommentDto commentDto)
			throws NotFoundException {
		commentService.addComment(topicId, commentDto);
	}

	@PostMapping("/{commentId}")
	@ResponseStatus(HttpStatus.CREATED)
	public void addLike(@PathVariable int commentId)
			throws NotFoundException {
		commentService.addLike(commentId);
	}

	@PutMapping("/{commentId}")
	public void modifyComment(@PathVariable int commentId ,@Valid @RequestBody CommentDto commentDto)
			throws UnauthorizedException, NotFoundException {
		commentService.modifyComment(commentId, commentDto);
	}

	@DeleteMapping("/{commentId}")
	public void deleteComment(@PathVariable int commentId)
			throws UnauthorizedException, NotFoundException {
		commentService.deleteComment(commentId);
	}


}
