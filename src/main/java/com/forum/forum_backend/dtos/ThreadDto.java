package com.forum.forum_backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ThreadDto {

	private int id;
	@NotEmpty(message = "Title cannot be empty")
	private String title;
	@NotEmpty(message = "Message cannot be empty")
	private String message;
	private UserDto threadAuthor;
	private Integer postsAmount;
	private int likesAmount;
	private List<PostDto> posts;


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

	public UserDto getThreadAuthor() {
		return threadAuthor;
	}

	public void setThreadAuthor(UserDto threadAuthor) {
		this.threadAuthor = threadAuthor;
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

	public List<PostDto> getPosts() {
		return posts;
	}

	public void setPosts(List<PostDto> posts) {
		this.posts = posts;
	}
}
