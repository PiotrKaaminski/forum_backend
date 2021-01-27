package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.ChatMessageDto;
import com.forum.forum_backend.exceptions.UserNotFoundException;
import com.forum.forum_backend.services.interfaces.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketController {

	private ChatService chatService;

	public WebSocketController(ChatService chatService) {
		this.chatService = chatService;
	}

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private SimpUserRegistry simpUserRegistry;

	@MessageMapping("/chat")
	public void chat(String message, Principal author) throws InterruptedException, UserNotFoundException {
		Thread.sleep(500);
		chatService.commandCheck(message.trim(), author.getName());
	}

	@MessageMapping("/whisper")
	public void whisper(ChatMessageDto message, Principal author) throws InterruptedException, UserNotFoundException {
		Thread.sleep(500);
		String destination = message.getUsername();
		message.setUsername(author.getName());
		if (simpUserRegistry.getUser(destination) == null) {
			throw new UserNotFoundException(destination + " is offline.", author.getName());
		} else {
			simpMessagingTemplate.convertAndSendToUser(destination, "/queue/whisper", message);
		}
	}

	@MessageExceptionHandler
	public void handleUserNotFoundException(UserNotFoundException ex) {
		ChatMessageDto whisper = new ChatMessageDto("Error", ex.getMessage());
		simpMessagingTemplate.convertAndSendToUser(ex.getAuthor(), "/queue/whisper", whisper);
	}

}
