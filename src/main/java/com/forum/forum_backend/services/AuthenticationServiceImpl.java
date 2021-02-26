package com.forum.forum_backend.services;

import com.forum.forum_backend.config.JwtTokenProvider;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.services.interfaces.AuthenticationService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final UserService userService;

	public AuthenticationServiceImpl(AuthenticationManager authenticationManager,
									 JwtTokenProvider jwtTokenProvider, UserService userService) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
		this.userService = userService;
	}

	@Override
	public UserDto login(UserDto userDto) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						userDto.getUsername(),
						userDto.getPassword()
				)
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDto user = userService.myAccountInfo();
		user.setJwt(jwtTokenProvider.generateToken(authentication));

		return user;
	}

}
