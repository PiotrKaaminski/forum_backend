package com.forum.forum_backend.dtos;

public class CommentDto {

	private int id;
	private String content;
	private UserDto commentAuthor;

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
}
