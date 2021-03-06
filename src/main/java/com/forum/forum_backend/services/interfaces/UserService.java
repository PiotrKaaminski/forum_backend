package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.dtos.PaginatedResponse;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.models.ForumEntity;
import com.forum.forum_backend.models.UserEntity;

import java.util.List;

public interface UserService {

	UserDto getUser(int userId) throws NotFoundException;
	UserPrincipal createUserPrincipal(int userId);
	UserEntity getUserById(int userId);
	boolean isUserAnAuthor(UserEntity entryAuthor);
	boolean isUserPermittedToModerate(ForumEntity forumEntity);
	boolean isUserPermittedToModerate(ForumEntity forumEntity, UserEntity user);
	void addUser(UserDto userDto);
	UserDto myAccountInfo();
	PaginatedResponse<UserDto> getUsers(String username, int size, int page);
	List<UserEntity> setLikes(List<UserEntity> likes, boolean liked);
}
