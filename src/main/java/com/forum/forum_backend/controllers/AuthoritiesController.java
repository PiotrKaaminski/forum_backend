package com.forum.forum_backend.controllers;

import com.forum.forum_backend.services.interfaces.AuthoritiesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/authorities")
public class AuthoritiesController {

	final private AuthoritiesService authoritiesService;

	public AuthoritiesController(AuthoritiesService authoritiesService) {
		this.authoritiesService = authoritiesService;
	}

	@GetMapping
	public List<String> getAuthorities() {
		return authoritiesService.getAuthorities();
	}
}
