package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.WhisperDto;
import com.forum.forum_backend.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketController {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private SimpUserRegistry simpUserRegistry;

	@MessageMapping("/chat")
	public String chat(String message) throws InterruptedException {
		Thread.sleep(500);
		return "New message sent: " + message;
	}

	@MessageMapping("/whisper")
	public void whisper(WhisperDto message, Principal principal) throws InterruptedException, UserNotFoundException {
		Thread.sleep(500);
		String destination = message.getUsername();
		message.setUsername(principal.getName());
		if (simpUserRegistry.getUser(destination) == null) {
			throw new UserNotFoundException(destination + " is offline.", principal.getName());
		} else {
			simpMessagingTemplate.convertAndSendToUser(destination, "/queue/whisper", message);
		}
	}

	@MessageExceptionHandler
	public void handleUserNotFoundException(UserNotFoundException ex) {
		WhisperDto whisper = new WhisperDto();
		whisper.setUsername("Error");
		whisper.setMessage(ex.getMessage());
		simpMessagingTemplate.convertAndSendToUser(ex.getAuthor(), "/queue/whisper", whisper);
	}

}
