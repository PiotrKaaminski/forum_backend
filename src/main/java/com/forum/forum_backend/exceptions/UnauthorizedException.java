package com.forum.forum_backend.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends Exception {

	private HttpStatus httpStatus = HttpStatus.FORBIDDEN;

	public UnauthorizedException(String message) { super(message); }

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
}
