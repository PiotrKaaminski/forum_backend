package com.forum.forum_backend.dtos;

import java.util.Date;

public class ExceptionDto {

	final private Date timestamp = new Date(System.currentTimeMillis());
	private int status;
	private String error;
	private String message;
	private String path;

	public Date getTimestamp() {
		return timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
