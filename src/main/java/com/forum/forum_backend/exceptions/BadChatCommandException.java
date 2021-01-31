package com.forum.forum_backend.exceptions;

public class BadChatCommandException extends Exception {

	private String author;

	public BadChatCommandException(String message, String author) {
		super(message);
		this.author = author;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}
