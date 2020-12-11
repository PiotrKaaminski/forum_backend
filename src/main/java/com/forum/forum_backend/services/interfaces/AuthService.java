package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.exceptions.InvalidCredentialsException;

public interface AuthService {
	void registerUser(UserDto userDto) throws InvalidCredentialsException;
	String login(UserDto userDto);
}
