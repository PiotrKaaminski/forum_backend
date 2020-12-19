package com.forum.forum_backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDto {

	private Integer id;
	private String title;
	private Integer parentId;
	private Integer childrenAmount;
	private List<CategoryDto> childCategories;
	private List<TopicDto> topics;

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

	public List<CategoryDto> getChildCategories() {
		return childCategories;
	}

	public void setChildCategories(List<CategoryDto> childCategories) {
		this.childCategories = childCategories;
	}

	public List<TopicDto> getTopics() {
		return topics;
	}

	public void setTopics(List<TopicDto> topics) {
		this.topics = topics;
	}

}
