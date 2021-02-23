package com.forum.forum_backend.dtos;

import com.forum.forum_backend.enums.Permission;

public class PermissionDto {

	private Permission name;
	private int forumId;

	public Permission getName() {
		return name;
	}

	public void setName(Permission name) {
		this.name = name;
	}

	public int getForumId() {
		return forumId;
	}

	public void setForumId(int forumId) {
		this.forumId = forumId;
	}
}
