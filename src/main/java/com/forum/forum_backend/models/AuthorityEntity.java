package com.forum.forum_backend.models;

import javax.persistence.*;

@Entity
@Table(name = "authority")
public class AuthorityEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "name")
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String authority) {
		this.name = authority;
	}
}
