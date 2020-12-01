package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.config.UserPrincipal;

public interface UserService {

	UserPrincipal createUserPrincipal(int userId);

}
