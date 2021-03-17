package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.ThreadDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.ThreadEntity;

public interface ThreadService {

	ThreadDto getThread(int threadId, int size, int page) throws NotFoundException;
	ThreadDto addThread(ThreadDto threadDto) throws NotFoundException, UnauthorizedException;
	void deleteThread(int threadId) throws UnauthorizedException, NotFoundException;
	ThreadDto mapChildEntityToDto(ThreadEntity threadEntity);
	ThreadDto modifyThread(int threadId, ThreadDto threadDto) throws NotFoundException, UnauthorizedException;
}
