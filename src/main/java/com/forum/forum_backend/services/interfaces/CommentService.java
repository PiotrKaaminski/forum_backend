package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.CommentDto;

public interface CommentService {

	void addComment(CommentDto commentDto, int topicId);

}
