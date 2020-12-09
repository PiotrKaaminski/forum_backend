package com.forum.forum_backend.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends Exception {

	final private HttpStatus httpStatus = HttpStatus.FORBIDDEN;

	public UnauthorizedException(String message) { super(message); }

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
