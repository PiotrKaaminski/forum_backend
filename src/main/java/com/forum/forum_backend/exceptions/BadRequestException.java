package com.forum.forum_backend.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends Exception{
	private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

	public BadRequestException(String message) { super(message); }

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
}
