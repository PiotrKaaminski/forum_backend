package com.forum.forum_backend.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "topic")
public class TopicEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "header")
	private String header;

	@Column(name = "content")
	private String content;

	@ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "category_id")
	private CategoryEntity parentCategory;

	@ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "topic_id")
	private List<CommentEntity> comments;

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(name = "topic_likes", joinColumns = @JoinColumn(name = "topic_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<UserEntity> usersLikes;

	public TopicEntity() { }

	public TopicEntity(String header, String content, UserEntity user) {
		this.header = header;
		this.content = content;
		this.user = user;
	}

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

	public CategoryEntity getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(CategoryEntity parentCategory) {
		this.parentCategory = parentCategory;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public List<CommentEntity> getComments() {
		return comments;
	}

	public void setComments(List<CommentEntity> comments) {
		this.comments = comments;
	}

	public List<UserEntity> getUsersLikes() {
		return usersLikes;
	}

	public void setUsersLikes(List<UserEntity> usersLikes) {
		this.usersLikes = usersLikes;
	}

	// helper methods

	public void addComment(CommentEntity commentEntity) {
		if (this.comments == null) {
			comments = new ArrayList<>();
		}
		comments.add(commentEntity);
	}

	public void addLike(UserEntity userEntity) {
		if (this.usersLikes == null) {
			usersLikes = new ArrayList<>();
		}
		usersLikes.add(userEntity);
	}

}
