package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.LockThreadDto;
import com.forum.forum_backend.dtos.ThreadDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.ThreadEntity;

public interface ThreadService {

	ThreadDto getThread(int threadId, int size, int page) throws NotFoundException;
	ThreadDto addThread(ThreadDto threadDto) throws NotFoundException;
	void addLike(int threadId) throws NotFoundException;
	void modifyThread(ThreadDto threadDto, int threadId) throws UnauthorizedException, NotFoundException;
	void deleteThread(int threadId) throws UnauthorizedException, NotFoundException;
	ThreadDto mapChildEntityToDto(ThreadEntity threadEntity);
	void toggleLocked(int threadId, LockThreadDto lockThreadDto) throws NotFoundException, UnauthorizedException;
}
