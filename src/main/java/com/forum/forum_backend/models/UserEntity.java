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
	private Collection<AuthorityEntity> authorities;

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(name = "topic_likes", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "topic_id"))
	private List<ThreadEntity> likedTopics ;

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(name = "comment_likes", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "comment_id"))
	private List<CommentEntity> likedComments;

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(name = "category_moderators", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
	private List<CategoryEntity> moderatedCategories;

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

	public List<ThreadEntity> getLikedTopics() {
		return likedTopics;
	}

	public void setLikedTopics(List<ThreadEntity> likedTopics) {
		this.likedTopics = likedTopics;
	}

	public List<CommentEntity> getLikedComments() {
		return likedComments;
	}

	public void setLikedComments(List<CommentEntity> likedComments) {
		this.likedComments = likedComments;
	}

	public List<CategoryEntity> getModeratedCategories() {
		return moderatedCategories;
	}

	public void setModeratedCategories(List<CategoryEntity> moderatedCategories) {
		this.moderatedCategories = moderatedCategories;
	}

	// helper methods

	public void addAuthority(AuthorityEntity authorityEntity) {
		if (this.authorities == null) {
			authorities = new ArrayList<>();
		}
		authorities.add(authorityEntity);

	}

	public void addTopicLike(ThreadEntity threadEntity) {
		if (this.likedTopics == null) {
			likedTopics = new ArrayList<>();
		}
		likedTopics.add(threadEntity);

	}

	public void addCommentLike(CommentEntity commentEntity) {
		if (this.likedComments == null) {
			likedComments = new ArrayList<>();
		}
		likedComments.add(commentEntity);

	}

	public void addModeratedCategory(CategoryEntity categoryEntity) {
		if (this.moderatedCategories == null) {
			moderatedCategories = new ArrayList<>();
		}
		moderatedCategories.add(categoryEntity);

	}
}
