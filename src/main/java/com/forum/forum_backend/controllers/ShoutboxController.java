package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.ChatMessageDto;
import com.forum.forum_backend.enums.ShoutboxMessageStatus;
import com.forum.forum_backend.exceptions.BadChatCommandException;
import com.forum.forum_backend.services.interfaces.ChatService;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ShoutboxController {

	private final ChatService chatService;
	private final SimpMessagingTemplate simpMessagingTemplate;

	public ShoutboxController(ChatService chatService, SimpMessagingTemplate simpMessagingTemplate) {
		this.chatService = chatService;
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	@MessageMapping("/chat")
	public void chat(String message, Principal author) throws BadChatCommandException {
		chatService.commandCheck(message.trim(), author.getName());
	}

	@MessageExceptionHandler
	public void handleCommandNotFoundException(BadChatCommandException ex) {
		ChatMessageDto whisper = new ChatMessageDto(ex.getMessage(), ShoutboxMessageStatus.ERROR);
		simpMessagingTemplate.convertAndSendToUser(ex.getAuthor(), "/queue/whisper", whisper);
	}

}
