package com.forum.forum_backend.models;

import javax.persistence.*;
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

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "category_id")
	private List<TopicEntity> topicEntities;

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

	public List<TopicEntity> getTopicEntities() {
		return topicEntities;
	}

	public void setTopicEntities(List<TopicEntity> topicEntities) {
		this.topicEntities = topicEntities;
	}
}
