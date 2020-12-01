package com.forum.forum_backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}

	@GetMapping("/hello2")
	public String hello2() {
		return "hello2";
	}

}
