package com.forum.forum_backend.services;

import com.forum.forum_backend.dtos.ChatMessageDto;
import com.forum.forum_backend.exceptions.BadChatCommandException;
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
	public void commandCheck(String message, String author) throws BadChatCommandException{
		if (message.startsWith("/")) {
			String command = message.split(" ", 2)[0];
			switch (command) {
				case "/msg" -> whisper(message, author);
				default -> throw new BadChatCommandException("Komenda \"" + command + "\" nie istnieje", author);
			}
		} else {
			ChatMessageDto chatMessageDto = new ChatMessageDto(author, message);
			simpMessagingTemplate.convertAndSend("/topic/chat", chatMessageDto);
		}
	}

	private void whisper(String message, String author) throws BadChatCommandException {
		String[] commandArgs = message.split(" ", 3);
		if (commandArgs.length < 3) {
			throw new BadChatCommandException("Złe argumenty dla komendy /msg, wymagane argumenty: /msg [użytkownik] [wiadomość]", author);
		}
		ChatMessageDto chatMessageDto = new ChatMessageDto(author, commandArgs[2]);
		if (simpUserRegistry.getUser(commandArgs[1]) == null) {
			throw new BadChatCommandException(commandArgs[1] + " jest niedostępny.", author);
		} else if (commandArgs[1].equals(author)) {
			throw new BadChatCommandException("Nie możesz wysłać wiadomości do samego siebie", author);
		} else {
			simpMessagingTemplate.convertAndSendToUser(commandArgs[1], "/queue/whisper", chatMessageDto);
			simpMessagingTemplate.convertAndSendToUser(author, "/queue/whisper", chatMessageDto);
		}
	}

}
