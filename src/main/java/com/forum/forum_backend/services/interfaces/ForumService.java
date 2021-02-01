package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.ForumDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;

import java.util.List;

public interface ForumService {
	List<ForumDto> getMainCategoryList();
	ForumDto getSubForum(int categoryId) throws NotFoundException;
	void addMainForum(ForumDto forumDto);
	void addSubForum(ForumDto forumDto, int parentCategoryId) throws NotFoundException, UnauthorizedException;
	void modifyForum(ForumDto forumDto, int categoryId) throws NotFoundException, UnauthorizedException;
	void deleteForum(int categoryId) throws NotFoundException, UnauthorizedException;
}
