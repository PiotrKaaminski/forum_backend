package com.forum.forum_backend.models;

import com.forum.forum_backend.config.UserPrincipal;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
public class CategoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "title")
	private String title;

	@ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "parent_category_id")
	private CategoryEntity parentCategory;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "parent_category_id")
	private List<CategoryEntity> childCategories;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "parentCategory")
	private List<ThreadEntity> topicEntities;

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(name = "category_moderators", joinColumns = @JoinColumn(name = "category_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<UserEntity> moderators;

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

	public CategoryEntity getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(CategoryEntity parentCategory) {
		this.parentCategory = parentCategory;
	}

	public List<CategoryEntity> getChildCategories() {
		return childCategories;
	}

	public void setChildCategories(List<CategoryEntity> childCategories) {
		this.childCategories = childCategories;
	}

	public List<ThreadEntity> getTopicEntities() {
		return topicEntities;
	}

	public void setTopicEntities(List<ThreadEntity> topicEntities) {
		this.topicEntities = topicEntities;
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

	public boolean isUserModerator(UserPrincipal user) {
		for (UserEntity moderator : moderators) {
			if (moderator.getId() == user.getId()) {
				return true;
			}
		}
		return false;
	}
}
