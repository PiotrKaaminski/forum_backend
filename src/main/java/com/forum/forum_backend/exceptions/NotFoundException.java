package com.forum.forum_backend.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends Exception{

	 private HttpStatus httpStatus = HttpStatus.NOT_FOUND;

	public NotFoundException(String message) {
		super(message);
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
}
