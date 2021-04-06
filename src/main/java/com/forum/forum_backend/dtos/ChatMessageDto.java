package com.forum.forum_backend.dtos;

import com.forum.forum_backend.enums.ShoutboxMessageStatus;

import java.sql.Timestamp;

public class ChatMessageDto {

	private String username;
	private String message;
	private Timestamp createTime;
	private ShoutboxMessageStatus shoutboxMessageStatus = ShoutboxMessageStatus.OK;

	public ChatMessageDto(String username, String message) {
		this.username = username;
		this.message = message;
		this.createTime = new Timestamp(System.currentTimeMillis());
	}

	public ChatMessageDto(String message, ShoutboxMessageStatus shoutboxMessageStatus) {
		this.message = message;
		this.createTime = new Timestamp(System.currentTimeMillis());
		this.shoutboxMessageStatus = shoutboxMessageStatus;
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

	public ShoutboxMessageStatus getShoutboxMessageStatus() {
		return shoutboxMessageStatus;
	}

	public void setShoutboxMessageStatus(ShoutboxMessageStatus shoutboxMessageStatus) {
		this.shoutboxMessageStatus = shoutboxMessageStatus;
	}

}
