package com.forum.forum_backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotEmpty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {

	private int id;
	@NotEmpty(message = "Message cannot be empty")
	private String message;
	private UserDto postAuthor;
	private int likesAmount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public UserDto getPostAuthor() {
		return postAuthor;
	}

	public void setPostAuthor(UserDto postAuthor) {
		this.postAuthor = postAuthor;
	}

	public int getLikesAmount() {
		return likesAmount;
	}

	public void setLikesAmount(int likesAmount) {
		this.likesAmount = likesAmount;
	}
}
