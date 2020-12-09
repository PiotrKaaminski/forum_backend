package com.forum.forum_backend.dtos;

import com.fasterxml.jackson.annotation.JsonView;
import com.forum.forum_backend.views.TopicView;

public class UserDto {

	@JsonView(TopicView.Minimal.class)
	private int id;

	@JsonView(TopicView.Minimal.class)
	private String username;
	private String password;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
