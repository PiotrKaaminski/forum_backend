package com.forum.forum_backend.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends Exception {

	final private HttpStatus httpStatus = HttpStatus.FORBIDDEN;

	public InvalidCredentialsException(String message) {
		super(message);
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

}
