package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.CommentDto;
import com.forum.forum_backend.services.interfaces.CommentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

	private final CommentService commentService;

	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}

	@PostMapping
	public void addComment(@RequestParam(name = "topicId") int topicId, @RequestBody CommentDto commentDto) {
		commentService.addComment(topicId, commentDto);
	}

	@PutMapping("/{commentId}")
	public void modifyComment(@PathVariable int commentId ,@RequestBody CommentDto commentDto) {
		commentService.modifyComment(commentId, commentDto);
	}

	@DeleteMapping("/{commentId}")
	public void deleteComment(@PathVariable int commentId) {
		commentService.deleteComment(commentId);
	}


}
