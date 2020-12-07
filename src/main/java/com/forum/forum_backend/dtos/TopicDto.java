package com.forum.forum_backend.dtos;

import java.util.List;

public class TopicDto {

	private int id;
	private String header;
	private String content;
	private UserDto topicAuthor;
	private List<CommentDto> comments;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public UserDto getTopicAuthor() {
		return topicAuthor;
	}

	public void setTopicAuthor(UserDto topicAuthor) {
		this.topicAuthor = topicAuthor;
	}

	public List<CommentDto> getComments() {
		return comments;
	}

	public void setComments(List<CommentDto> comments) {
		this.comments = comments;
	}

}
