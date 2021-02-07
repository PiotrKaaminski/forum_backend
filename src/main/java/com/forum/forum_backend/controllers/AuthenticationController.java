package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.services.interfaces.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

	private final AuthenticationService authService;

	public AuthenticationController(AuthenticationService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public UserDto authenticateUser(@RequestBody UserDto userDto) {return authService.login(userDto);}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public void register(@Valid @RequestBody UserDto userDto) {
		authService.registerUser(userDto);
	}

}
