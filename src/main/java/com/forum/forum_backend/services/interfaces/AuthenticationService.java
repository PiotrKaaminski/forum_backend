package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.UserDto;

public interface AuthenticationService {
	void registerUser(UserDto userDto);
	UserDto login(UserDto userDto);
}
