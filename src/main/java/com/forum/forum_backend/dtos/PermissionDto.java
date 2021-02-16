package com.forum.forum_backend.dtos;

public class PermissionDto {

	private String name;
	private int forumId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getForumId() {
		return forumId;
	}

	public void setForumId(int forumId) {
		this.forumId = forumId;
	}
}
