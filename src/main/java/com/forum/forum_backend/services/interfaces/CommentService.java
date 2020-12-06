package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.CommentDto;

public interface CommentService {

	void addComment(int topicId, CommentDto commentDto);
	void modifyComment(int commentId, CommentDto commentDto);
	void deleteComment(int commentId);

}
