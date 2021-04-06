package com.forum.forum_backend.dtos;

import com.forum.forum_backend.enums.Permission;

import java.util.ArrayList;
import java.util.List;

public class PermissionDto {

	private Permission name;
	private List<Integer> forumIdList = new ArrayList<>();

	public Permission getName() {
		return name;
	}

	public void setName(Permission name) {
		this.name = name;
	}

	public List<Integer> getForumIdList() {
		return forumIdList;
	}

	public void setForumIdList(List<Integer> forumIdList) {
		this.forumIdList = forumIdList;
	}

	// helper methods

	public void addForumId(Integer forumId) {
		if (forumIdList == null) {
			forumIdList = new ArrayList<>();
		}
		forumIdList.add(forumId);
	}
}
