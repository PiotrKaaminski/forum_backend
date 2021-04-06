package com.forum.forum_backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ForumDto {

	private Integer id;
	@NotEmpty(message = "Forum title cannot be empty")
	private String title;
	@NotEmpty(message = "Forum description cannot be empty")
	private String description;
	private Timestamp createTime;
	private ForumDto parent;
	private Integer threadsAmount;
	private List<ForumDto> forums;
	private List<ThreadDto> threads;
	private List<ForumDto> breadcrumb;
	private boolean canModerate;
	private ThreadDto latestThread;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public ForumDto getParent() {
		return parent;
	}

	public void setParent(ForumDto parent) {
		this.parent = parent;
	}

	public Integer getThreadsAmount() {
		return threadsAmount;
	}

	public void setThreadsAmount(Integer threadsAmount) {
		this.threadsAmount = threadsAmount;
	}

	public List<ForumDto> getForums() {
		return forums;
	}

	public void setForums(List<ForumDto> forums) {
		this.forums = forums;
	}

	public List<ThreadDto> getThreads() {
		return threads;
	}

	public void setThreads(List<ThreadDto> threads) {
		this.threads = threads;
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

	public ThreadDto getLatestThread() {
		return latestThread;
	}

	public void setLatestThread(ThreadDto latestThread) {
		this.latestThread = latestThread;
	}

	//helper methods

	public void addBreadcrumb(ForumDto forumDto) {
		if (this.breadcrumb == null) {
			breadcrumb = new ArrayList<>();
		}
		breadcrumb.add(forumDto);
	}
}
