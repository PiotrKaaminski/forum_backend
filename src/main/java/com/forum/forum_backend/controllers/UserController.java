package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/me")
	public UserDto myAccountInfo() {
		return userService.myAccountInfo();
	}

	@PostMapping
	public void addUser(@Valid @RequestBody UserDto userDto){
		userService.addUser(userDto);
	}
}
