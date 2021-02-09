package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.UserDto;

public interface AuthenticationService {
	UserDto login(UserDto userDto);
}
