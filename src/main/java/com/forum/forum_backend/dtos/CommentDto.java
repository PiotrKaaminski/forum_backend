package com.forum.forum_backend.dtos;

import com.fasterxml.jackson.annotation.JsonView;
import com.forum.forum_backend.views.TopicView;

@JsonView(TopicView.Extended.class)
public class CommentDto {

	private int id;
	private String content;
	private UserDto commentAuthor;
	private int likesAmount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public UserDto getCommentAuthor() {
		return commentAuthor;
	}

	public void setCommentAuthor(UserDto commentAuthor) {
		this.commentAuthor = commentAuthor;
	}

	public int getLikesAmount() {
		return likesAmount;
	}

	public void setLikesAmount(int likesAmount) {
		this.likesAmount = likesAmount;
	}
}
