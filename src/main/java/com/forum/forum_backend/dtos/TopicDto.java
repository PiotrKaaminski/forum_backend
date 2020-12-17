package com.forum.forum_backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TopicDto {

	private int id;
	@NotEmpty(message = "Header cannot be empty")
	private String header;
	@NotEmpty(message = "Content cannot be empty")
	private String content;
	private UserDto topicAuthor;
	private Integer commentsAmount;
	private int likesAmount;
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

	public Integer getCommentsAmount() {
		return commentsAmount;
	}

	public void setCommentsAmount(Integer commentsAmount) {
		this.commentsAmount = commentsAmount;
	}

	public int getLikesAmount() {
		return likesAmount;
	}

	public void setLikesAmount(int likesAmount) {
		this.likesAmount = likesAmount;
	}

	public List<CommentDto> getComments() {
		return comments;
	}

	public void setComments(List<CommentDto> comments) {
		this.comments = comments;
	}
	
}
