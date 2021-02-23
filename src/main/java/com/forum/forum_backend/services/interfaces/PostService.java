package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.PaginatedResponse;
import com.forum.forum_backend.dtos.PostDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;

public interface PostService {

	PostDto getPost(int postId) throws NotFoundException;
	PaginatedResponse<PostDto> getPostsByThread(int threadId, int size, int page);
	PostDto addPost(int threadId, PostDto postDto) throws NotFoundException, UnauthorizedException;
	void addLike(int postId) throws NotFoundException;
	PostDto modifyPost(int postId, PostDto postDto) throws UnauthorizedException, NotFoundException;
	void deletePost(int postId) throws UnauthorizedException, NotFoundException;


}
