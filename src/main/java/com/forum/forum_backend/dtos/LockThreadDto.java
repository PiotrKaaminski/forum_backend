package com.forum.forum_backend.dtos;

import javax.validation.constraints.NotNull;

public class LockThreadDto {

	@NotNull(message = "Locked cannot be null to switch locked state")
	private boolean locked;

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}
