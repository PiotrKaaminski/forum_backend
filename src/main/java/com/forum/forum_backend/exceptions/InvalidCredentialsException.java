package com.forum.forum_backend.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends Exception {

	private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

	public InvalidCredentialsException(String message) {
		super(message);
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
}
