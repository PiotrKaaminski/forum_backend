package com.forum.forum_backend.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Entity
@Table(name = "forum")
public class ForumEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "title")
	private String title;

	@Column(name = "description")
	private String description;

	@Column(name = "create_time")
	private Timestamp createTime;

	@ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "parent_forum_id")
	private ForumEntity parentForum;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "parent_forum_id")
	private List<ForumEntity> childForums = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "parentForum")
	private List<ThreadEntity> threadEntities = new ArrayList<>();

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(name = "forum_moderators", joinColumns = @JoinColumn(name = "forum_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<UserEntity> moderators = new ArrayList<>();

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

	public ForumEntity getParentForum() {
		return parentForum;
	}

	public void setParentForum(ForumEntity parentForum) {
		this.parentForum = parentForum;
	}

	public List<ForumEntity> getChildForums() {
		return childForums;
	}

	public void setChildForums(List<ForumEntity> childForums) {
		this.childForums = childForums;
	}

	public List<ThreadEntity> getThreadEntities() {
		return threadEntities;
	}

	public void setThreadEntities(List<ThreadEntity> threadEntities) {
		this.threadEntities = threadEntities;
	}

	public List<UserEntity> getModerators() {
		return moderators;
	}

	public void setModerators(List<UserEntity> moderators) {
		this.moderators = moderators;
	}

	// helper methods

	public void addModerator(UserEntity userEntity) {
		if (this.moderators == null) {
			moderators = new ArrayList<>();
		}
		moderators.add(userEntity);
	}

	public boolean isUserModerator(UserEntity user) {
		return moderators.stream().anyMatch(Predicate.isEqual(user));
	}
}
