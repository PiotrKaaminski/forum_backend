package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.PostDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;

public interface PostService {

	void addPost(int threadId, PostDto postDto) throws NotFoundException;
	void addLike(int postId) throws NotFoundException;
	void modifyPost(int postId, PostDto postDto) throws UnauthorizedException, NotFoundException;
	void deletePost(int postId) throws UnauthorizedException, NotFoundException;


}
