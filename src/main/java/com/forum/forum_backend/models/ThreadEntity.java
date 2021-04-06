package com.forum.forum_backend.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "thread")
public class ThreadEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "title")
	private String title;

	@Column(name = "message")
	private String message;

	@Column(name = "create_time")
	private Timestamp createTime;

	@Column(name = "locked")
	private boolean locked;

	@ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "forum_id")
	private ForumEntity parentForum;

	@ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "creator_id")
	private UserEntity user;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "thread")
	private List<PostEntity> posts = new ArrayList<>();

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(name = "thread_likes", joinColumns = @JoinColumn(name = "thread_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<UserEntity> usersLikes = new ArrayList<>();

	public ThreadEntity() { }

	public ThreadEntity(String title, String message, UserEntity user, Timestamp createTime) {
		this.title = title;
		this.message = message;
		this.user = user;
		this.createTime = createTime;
	}

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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public ForumEntity getParentForum() {
		return parentForum;
	}

	public void setParentForum(ForumEntity parentForum) {
		this.parentForum = parentForum;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public List<PostEntity> getPosts() {
		return posts;
	}

	public void setPosts(List<PostEntity> posts) {
		this.posts = posts;
	}

	public List<UserEntity> getUsersLikes() {
		return usersLikes;
	}

	public void setUsersLikes(List<UserEntity> usersLikes) {
		this.usersLikes = usersLikes;
	}

	// helper methods

	public void addPost(PostEntity postEntity) {
		if (this.posts == null) {
			posts = new ArrayList<>();
		}
		posts.add(postEntity);
	}

	public void addLike(UserEntity userEntity) {
		if (this.usersLikes == null) {
			usersLikes = new ArrayList<>();
		}
		usersLikes.add(userEntity);
	}

}
