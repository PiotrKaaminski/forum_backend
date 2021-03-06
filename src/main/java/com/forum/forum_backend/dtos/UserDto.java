package com.forum.forum_backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.forum.forum_backend.validators.annotations.NoSpace;
import com.forum.forum_backend.validators.annotations.UsernameUnique;
import org.hibernate.validator.constraints.Length;

import java.sql.Timestamp;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

	private int id;
	@NoSpace
	@Length(min = 5, max = 25, message = "Username cannot be shorter than 5 and longer than 25")
	@UsernameUnique
	private String username;
	@NoSpace
	@Length(min = 5, max = 25, message = "Password cannot be shorter than 5 and longer than 25")
	private String password;
	private String email = "user@forum.com"; //email will be implemented
	private String jwt;
	private Timestamp joinTime;
	private PermissionDto authority;

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
		this.username = username.trim();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password.trim();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public Timestamp getJoinTime() {
		return joinTime;
	}

	public void setJoinTime(Timestamp joinTime) {
		this.joinTime = joinTime;
	}

	public PermissionDto getAuthority() {
		return authority;
	}

	public void setAuthority(PermissionDto authority) {
		this.authority = authority;
	}
}
