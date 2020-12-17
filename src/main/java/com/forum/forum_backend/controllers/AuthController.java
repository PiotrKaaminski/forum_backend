package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.services.interfaces.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public String authenticateUser(@RequestBody UserDto userDto) {return authService.login(userDto);}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public void register(@Valid @RequestBody UserDto userDto) {
		authService.registerUser(userDto);
	}

}
