package com.forum.forum_backend.services;

import com.forum.forum_backend.dtos.ChatMessageDto;
import com.forum.forum_backend.services.interfaces.ChatService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {

	private SimpMessagingTemplate simpMessagingTemplate;
	private SimpUserRegistry simpUserRegistry;

	public ChatServiceImpl(SimpMessagingTemplate simpMessagingTemplate, SimpUserRegistry simpUserRegistry) {
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.simpUserRegistry = simpUserRegistry;
	}

	@Override
	public void commandCheck(String message, String author) {
		if (message.startsWith("/")) {

		} else {
			ChatMessageDto chatMessageDto = new ChatMessageDto(author, message);
			simpMessagingTemplate.convertAndSend("/topic/chat", chatMessageDto);
		}
	}

}
