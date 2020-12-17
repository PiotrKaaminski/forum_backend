package com.forum.forum_backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.forum.forum_backend.validators.annotations.UsernameUnique;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

	private int id;
	@NotNull(message = "Username cannot be null")
	@Length(min = 5, max = 20, message = "Username cannot be shorter than 5 and longer than 20")
	@UsernameUnique
	private String username;
	@NotNull(message = "Password cannot be null")
	@Length(min = 5, max = 25, message = "Password cannot be shorter than 5 and longer than 25")
	private String password;

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

}
