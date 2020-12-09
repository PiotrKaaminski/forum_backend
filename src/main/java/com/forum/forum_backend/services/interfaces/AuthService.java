package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.exceptions.InvalidCredentialsException;

import java.sql.SQLIntegrityConstraintViolationException;

public interface AuthService {
	void registerUser(UserDto userDto) throws InvalidCredentialsException;
	String login(UserDto userDto);
}
