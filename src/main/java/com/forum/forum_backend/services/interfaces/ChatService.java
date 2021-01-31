package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.exceptions.BadChatCommandException;
import com.forum.forum_backend.exceptions.UserNotFoundException;

public interface ChatService {
	void commandCheck(String message, String author) throws BadChatCommandException, UserNotFoundException;
}
