package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.ChatMessageDto;
import com.forum.forum_backend.exceptions.BadChatCommandException;
import com.forum.forum_backend.exceptions.UserNotFoundException;
import com.forum.forum_backend.services.interfaces.ChatService;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketController {

	private final ChatService chatService;
	private final SimpMessagingTemplate simpMessagingTemplate;

	public WebSocketController(ChatService chatService, SimpMessagingTemplate simpMessagingTemplate) {
		this.chatService = chatService;
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	@MessageMapping("/chat")
	public void chat(String message, Principal author) throws InterruptedException, UserNotFoundException, BadChatCommandException {
		Thread.sleep(500);
		chatService.commandCheck(message.trim(), author.getName());
	}

	@MessageExceptionHandler
	public void handleUserNotFoundException(UserNotFoundException ex) {
		ChatMessageDto whisper = new ChatMessageDto("Error", ex.getMessage());
		simpMessagingTemplate.convertAndSendToUser(ex.getAuthor(), "/queue/whisper", whisper);
	}

	@MessageExceptionHandler
	public void handleCommandNotFoundException(BadChatCommandException ex) {
		ChatMessageDto whisper = new ChatMessageDto("Error", ex.getMessage());
		simpMessagingTemplate.convertAndSendToUser(ex.getAuthor(), "/queue/whisper", whisper);
	}

}
