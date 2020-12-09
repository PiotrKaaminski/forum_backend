package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.CommentDto;
import com.forum.forum_backend.exceptions.UnauthorizedException;

public interface CommentService {

	void addComment(int topicId, CommentDto commentDto);
	void modifyComment(int commentId, CommentDto commentDto) throws UnauthorizedException;
	void deleteComment(int commentId) throws UnauthorizedException;

}
