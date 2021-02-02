package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.models.ForumEntity;
import com.forum.forum_backend.models.UserEntity;

public interface UserService {

	UserPrincipal createUserPrincipal(int userId);
	UserEntity getUserById(int userId);
	boolean isUserAnAuthor(UserEntity entryAuthor);
	boolean isUserPermittedToModerate(ForumEntity forumEntity);
}
