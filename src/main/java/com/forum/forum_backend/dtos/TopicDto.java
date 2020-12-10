package com.forum.forum_backend.dtos;

import com.fasterxml.jackson.annotation.JsonView;
import com.forum.forum_backend.views.TopicView;

import java.util.List;

public class TopicDto {

	@JsonView(TopicView.Minimal.class)
	private int id;
	@JsonView(TopicView.Minimal.class)
	private String header;
	@JsonView(TopicView.Extended.class)
	private String content;
	@JsonView(TopicView.Minimal.class)
	private UserDto topicAuthor;
	@JsonView(TopicView.Extended.class)
	private List<CommentDto> comments;
	@JsonView(TopicView.TopicList.class)
	private int commentsAmount;

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

	public int getCommentsAmount() {
		return commentsAmount;
	}

	public void setCommentsAmount(int commentsAmount) {
		this.commentsAmount = commentsAmount;
	}
}
