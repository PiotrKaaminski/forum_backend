package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.services.interfaces.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

	private final AuthenticationService authService;

	public AuthenticationController(AuthenticationService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public UserDto authenticateUser(@RequestBody UserDto userDto) {return authService.login(userDto);}

}
