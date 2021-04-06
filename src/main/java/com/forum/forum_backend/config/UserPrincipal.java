package com.forum.forum_backend.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public class UserPrincipal implements UserDetails{

	private int id;
	private String username;
	private String password;
	private List<GrantedAuthority> authorities;

	public UserPrincipal(int id, String username, String password, List<GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public List<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	// helper methods

	public boolean hasAuthority(String authority) {
		if (authorities == null) {
			return false;
		} else {
			for (GrantedAuthority x : authorities) {
				if (x.getAuthority().equals(authority)) {
					return true;
				}
			}
		}
		return false;
	}

}
