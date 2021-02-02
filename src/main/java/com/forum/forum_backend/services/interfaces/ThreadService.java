package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.ThreadDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.ThreadEntity;

public interface ThreadService {

	ThreadDto getThread(int threadId) throws NotFoundException;
	void addThread(ThreadDto threadDto);
	void addLike(int threadId) throws NotFoundException;
	void modifyThread(ThreadDto threadDto, int threadId) throws UnauthorizedException, NotFoundException;
	void deleteThread(int threadId) throws UnauthorizedException, NotFoundException;
	ThreadDto mapChildEntityToDto(ThreadEntity threadEntity);
}
