package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.ForumDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;

import java.util.List;

public interface ForumService {
	List<ForumDto> getMainForumList();
	ForumDto getSubForum(int forumId) throws NotFoundException;
	void addMainForum(ForumDto forumDto);
	void addSubForum(ForumDto forumDto, int parentForumId) throws NotFoundException, UnauthorizedException;
	void modifyForum(ForumDto forumDto, int forumId) throws NotFoundException, UnauthorizedException;
	void deleteForum(int forumId) throws NotFoundException, UnauthorizedException;
}
