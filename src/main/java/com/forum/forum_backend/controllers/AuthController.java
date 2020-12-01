package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.services.interfaces.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/login")
	public String authenticateUser(@RequestBody UserDto userDto) {return authService.login(userDto);}

	@PostMapping("/register")
	public void register(@RequestBody UserDto userDto) {
		authService.registerUser(userDto);
	}

}
