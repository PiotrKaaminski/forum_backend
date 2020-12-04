package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.models.UserEntity;

public interface UserService {

	UserPrincipal createUserPrincipal(int userId);
	UserEntity getUserById(int userId);

}
