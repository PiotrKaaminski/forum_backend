package com.forum.forum_backend.dtos;

import java.sql.Timestamp;

public class ChatMessageDto {

	private String username;
	private String message;
	private Timestamp createTime;


	public ChatMessageDto(String username, String message) {
		this.username = username;
		this.message = message;
		this.createTime = new Timestamp(System.currentTimeMillis());
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
}
