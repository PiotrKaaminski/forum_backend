package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.UserDto;

public interface AuthService {
	void registerUser(UserDto userDto);
	String login(UserDto userDto);
}
