package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.CommentDto;
import com.forum.forum_backend.services.interfaces.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/topics/{topicId}")
public class CommentController {

	@Autowired
	private CommentService commentService;

	@PostMapping
	public void addComment(@RequestBody CommentDto commentDto, @PathVariable int topicId) {
		commentService.addComment(commentDto, topicId);
	}


}
