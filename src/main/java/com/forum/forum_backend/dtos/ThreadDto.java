package com.forum.forum_backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThreadDto {

	private int id;
	@NotEmpty(message = "Title cannot be empty")
	private String title;
	@NotEmpty(message = "Message cannot be empty")
	private String message;
	private UserDto creator;
	@NotNull(message = "Parent forum cannot be empty")
	private ForumDto forum;
	private Integer postsAmount;
	private int likesAmount;
	private Timestamp createTime;
	private PaginatedResponse<PostDto> posts;
	private List<ForumDto> breadcrumb;
	private boolean canModerate;
	private Boolean locked;
	private Boolean liked;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public UserDto getCreator() {
		return creator;
	}

	public void setCreator(UserDto creator) {
		this.creator = creator;
	}

	public ForumDto getForum() {
		return forum;
	}

	public void setForum(ForumDto forum) {
		this.forum = forum;
	}

	public Integer getPostsAmount() {
		return postsAmount;
	}

	public void setPostsAmount(Integer postsAmount) {
		this.postsAmount = postsAmount;
	}

	public int getLikesAmount() {
		return likesAmount;
	}

	public void setLikesAmount(int likesAmount) {
		this.likesAmount = likesAmount;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public PaginatedResponse<PostDto> getPosts() {
		return posts;
	}

	public void setPosts(PaginatedResponse<PostDto> posts) {
		this.posts = posts;
	}

	public List<ForumDto> getBreadcrumb() {
		return breadcrumb;
	}

	public void setBreadcrumb(List<ForumDto> breadcrumb) {
		this.breadcrumb = breadcrumb;
	}

	public boolean isCanModerate() {
		return canModerate;
	}

	public void setCanModerate(boolean canModerate) {
		this.canModerate = canModerate;
	}

	public Boolean isLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public Boolean isLiked() {
		return liked;
	}

	public void setLiked(Boolean liked) {
		this.liked = liked;
	}

	//helper methods

	public void addBreadcrumb(ForumDto forumDto) {
		if (this.breadcrumb == null) {
			breadcrumb = new ArrayList<>();
		}
		breadcrumb.add(forumDto);
	}
}
