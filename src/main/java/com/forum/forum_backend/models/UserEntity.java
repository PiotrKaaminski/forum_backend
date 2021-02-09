package com.forum.forum_backend.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user")
public class UserEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(name = "user_authorities", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "authority_id"))
	private Collection<AuthorityEntity> authorities = new ArrayList<>();

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(name = "thread_likes", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "thread_id"))
	private List<ThreadEntity> likedThreads = new ArrayList<>();

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(name = "post_likes", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "post_id"))
	private List<PostEntity> likedPosts = new ArrayList<>();

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(name = "forum_moderators", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "forum_id"))
	private List<ForumEntity> moderatedForums = new ArrayList<>();

	// Getters and setters


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Collection<AuthorityEntity> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<AuthorityEntity> authorities) {
		this.authorities = authorities;
	}

	public List<ThreadEntity> getLikedThreads() {
		return likedThreads;
	}

	public void setLikedThreads(List<ThreadEntity> likedThreads) {
		this.likedThreads = likedThreads;
	}

	public List<PostEntity> getLikedPosts() {
		return likedPosts;
	}

	public void setLikedPosts(List<PostEntity> likedPosts) {
		this.likedPosts = likedPosts;
	}

	public List<ForumEntity> getModeratedForums() {
		return moderatedForums;
	}

	public void setModeratedForums(List<ForumEntity> moderatedForums) {
		this.moderatedForums = moderatedForums;
	}

	// helper methods

	public void addAuthority(AuthorityEntity authorityEntity) {
		if (this.authorities == null) {
			authorities = new ArrayList<>();
		}
		authorities.add(authorityEntity);

	}

	public void addThreadLike(ThreadEntity threadEntity) {
		if (this.likedThreads == null) {
			likedThreads = new ArrayList<>();
		}
		likedThreads.add(threadEntity);

	}

	public void addPostLike(PostEntity postEntity) {
		if (this.likedPosts == null) {
			likedPosts = new ArrayList<>();
		}
		likedPosts.add(postEntity);

	}

	public void addModeratedForum(ForumEntity forumEntity) {
		if (this.moderatedForums == null) {
			moderatedForums = new ArrayList<>();
		}
		moderatedForums.add(forumEntity);

	}
}
