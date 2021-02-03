package com.forum.forum_backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ForumDto {

	private Integer id;
	@NotEmpty(message = "Forum title cannot be empty")
	private String title;
	private Integer parentId;
	private Integer childrenAmount;
	private List<ForumDto> childForums;
	private List<ThreadDto> threads;
	private List<ForumDto> breadcrump;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getChildrenAmount() {
		return childrenAmount;
	}

	public void setChildrenAmount(Integer childrenAmount) {
		this.childrenAmount = childrenAmount;
	}

	public List<ForumDto> getChildForums() {
		return childForums;
	}

	public void setChildForums(List<ForumDto> childForums) {
		this.childForums = childForums;
	}

	public List<ThreadDto> getThreads() {
		return threads;
	}

	public void setThreads(List<ThreadDto> threads) {
		this.threads = threads;
	}

	public List<ForumDto> getBreadcrump() {
		return breadcrump;
	}

	public void setBreadcrump(List<ForumDto> breadcrump) {
		this.breadcrump = breadcrump;
	}

	//helper methods

	public void addBreadcrump(ForumDto forumDto) {
		if (this.breadcrump == null) {
			breadcrump = new ArrayList<>();
		}
		breadcrump.add(forumDto);
	}
}
