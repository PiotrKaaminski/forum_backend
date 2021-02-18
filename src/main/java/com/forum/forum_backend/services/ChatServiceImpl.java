package com.forum.forum_backend.services;

import com.forum.forum_backend.dtos.ChatMessageDto;
import com.forum.forum_backend.exceptions.BadChatCommandException;
import com.forum.forum_backend.exceptions.UserNotFoundException;
import com.forum.forum_backend.services.interfaces.ChatService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {

	final private SimpMessagingTemplate simpMessagingTemplate;
	final private SimpUserRegistry simpUserRegistry;

	public ChatServiceImpl(SimpMessagingTemplate simpMessagingTemplate, SimpUserRegistry simpUserRegistry) {
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.simpUserRegistry = simpUserRegistry;
	}

	@Override
	public void commandCheck(String message, String author) throws BadChatCommandException, UserNotFoundException {
		if (message.startsWith("/")) {
			String command = message.split(" ", 2)[0];
			switch (command) {
				case "/msg" -> whisper(message, author);
				default -> throw new BadChatCommandException("Command \"" + command + "\" doesn't exist", author);
			}
		} else {
			ChatMessageDto chatMessageDto = new ChatMessageDto(author, message);
			simpMessagingTemplate.convertAndSend("/topic/chat", chatMessageDto);
		}
	}

	private void whisper(String message, String author) throws BadChatCommandException, UserNotFoundException {
		String[] commandArgs = message.split(" ", 3);
		if (commandArgs.length < 3) {
			throw new BadChatCommandException("Bad arguments for command /msg, need /msg [username] [message]", author);
		}
		ChatMessageDto chatMessageDto = new ChatMessageDto(author, commandArgs[2]);
		if (simpUserRegistry.getUser(commandArgs[1]) == null) {
			throw new UserNotFoundException(commandArgs[1] + " is offline.", author);
		} else {
			simpMessagingTemplate.convertAndSendToUser(commandArgs[1], "/queue/whisper", chatMessageDto);
			simpMessagingTemplate.convertAndSendToUser(author, "/queue/whisper", chatMessageDto);
		}
	}

}
