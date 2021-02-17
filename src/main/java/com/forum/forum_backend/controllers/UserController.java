package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.PaginatedResponse;
import com.forum.forum_backend.dtos.PermissionDto;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.services.interfaces.AuthoritiesService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	private final AuthoritiesService authoritiesService;

	public UserController(UserService userService, AuthoritiesService authoritiesService) {
		this.userService = userService;
		this.authoritiesService = authoritiesService;
	}

	@GetMapping
	public PaginatedResponse<UserDto> getUsers (
			@RequestParam(defaultValue = "", required = false) String username,
			@RequestParam(defaultValue = "3", required = false) int size,
			@RequestParam(defaultValue = "0", required = false) int page) {
		return userService.getUsers(username, size, page);
	}

	@GetMapping("/me")
	public UserDto myAccountInfo() {
		return userService.myAccountInfo();
	}

	@PostMapping
	public void addUser(@Valid @RequestBody UserDto userDto){
		userService.addUser(userDto);
	}

	@PatchMapping("/{userId}")
	public void assignPermission(@RequestBody PermissionDto permissionDto, @PathVariable int userId)
			throws NotFoundException, UnauthorizedException {
		authoritiesService.assignPermission(permissionDto, userId);
	}
}
