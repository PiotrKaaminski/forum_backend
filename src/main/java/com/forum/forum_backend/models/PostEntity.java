package com.forum.forum_backend.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
public class PostEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "message")
	private String message;

	@ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "thread_id")
	private ThreadEntity thread;

	@ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "creator_id")
	private UserEntity user;

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(name = "post_likes", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<UserEntity> usersLikes ;

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

	public ThreadEntity getThread() {
		return thread;
	}

	public void setThread(ThreadEntity thread) {
		this.thread = thread;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public List<UserEntity> getUsersLikes() {
		return usersLikes;
	}

	public void setUsersLikes(List<UserEntity> usersLikes) {
		this.usersLikes = usersLikes;
	}

	// helper methods

	public void addLike(UserEntity userEntity) {
		if (this.usersLikes == null) {
			usersLikes = new ArrayList<>();
		}
		usersLikes.add(userEntity);
	}
}
