package com.forum.forum_backend.exceptions;

import org.springframework.security.core.AuthenticationException;

public class BadJwtException extends AuthenticationException {

	public BadJwtException(String msg) {
		super(msg);
	}

}
