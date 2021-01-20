package com.forum.forum_backend.exceptions;

public class UserNotFoundException extends Exception {

	private String author;

	public UserNotFoundException(String message, String author) {
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
