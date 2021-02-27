package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.PaginatedResponse;
import com.forum.forum_backend.dtos.PermissionDto;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.exceptions.BadRequestException;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.services.interfaces.AuthoritiesService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	private final AuthoritiesService authoritiesService;
	@Value("${pagination.defaultSize}")
	private int defaultPaginationSize;

	public UserController(UserService userService, AuthoritiesService authoritiesService) {
		this.userService = userService;
		this.authoritiesService = authoritiesService;
	}

	@GetMapping
	public PaginatedResponse<UserDto> getUsers (
			@RequestParam(defaultValue = "", required = false) String username,
			@RequestParam(defaultValue = "0", required = false) int size,
			@RequestParam(defaultValue = "0", required = false) int page) {
		if (size == 0) {
			size = defaultPaginationSize;
		}
		return userService.getUsers(username, size, page);
	}

	@GetMapping("/me")
	public UserDto myAccountInfo() {
		return userService.myAccountInfo();
	}
	
	@GetMapping("/{userId}")
	public UserDto getUser(@PathVariable int userId) throws NotFoundException {
		return userService.getUser(userId);
	}

	@PostMapping
	public void addUser(@Valid @RequestBody UserDto userDto){
		userService.addUser(userDto);
	}

	@PatchMapping("/{userId}")
	public UserDto assignPermission(@RequestBody PermissionDto permissionDto, @PathVariable int userId)
			throws NotFoundException, UnauthorizedException, BadRequestException {
		return authoritiesService.assign(permissionDto, userId);
	}
}
