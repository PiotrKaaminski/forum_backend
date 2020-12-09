package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.CommentDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.services.interfaces.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

	private final CommentService commentService;

	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void addComment(@RequestParam(name = "topicId") int topicId, @RequestBody CommentDto commentDto)
			throws NotFoundException {
		commentService.addComment(topicId, commentDto);
	}

	@PutMapping("/{commentId}")
	public void modifyComment(@PathVariable int commentId ,@RequestBody CommentDto commentDto)
			throws UnauthorizedException, NotFoundException {
		commentService.modifyComment(commentId, commentDto);
	}

	@DeleteMapping("/{commentId}")
	public void deleteComment(@PathVariable int commentId)
			throws UnauthorizedException, NotFoundException {
		commentService.deleteComment(commentId);
	}


}
