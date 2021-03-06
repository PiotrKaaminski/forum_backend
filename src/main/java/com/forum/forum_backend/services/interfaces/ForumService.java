package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.ForumDto;
import com.forum.forum_backend.dtos.PaginatedResponse;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.ForumEntity;

import java.util.List;

public interface ForumService {
	PaginatedResponse<ForumDto> getMainForumList();
	ForumDto getSubForum(int forumId, boolean getThreads) throws NotFoundException;
	ForumDto addForum(ForumDto forumDto) throws NotFoundException, UnauthorizedException;
	ForumDto modifyForum(ForumDto forumDto, int forumId) throws NotFoundException, UnauthorizedException;
	void deleteForum(int forumId) throws NotFoundException, UnauthorizedException;
	List<ForumDto> getBreadcrumb(ForumEntity forumEntity);
}
