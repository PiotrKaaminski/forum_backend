package com.forum.forum_backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Rest endpoints to test authorization

@RestController
@RequestMapping("/api")
public class HelloController {

	@GetMapping("/hello1")
	public String hello1() {
		return "hello1";
	}

	@GetMapping("/hello2")
	public String hello2() {
		return "hello2";
	}

	@GetMapping("/hello3")
	public String hello3() {
		return "hello3";
	}

	@GetMapping("/hello4")
	public String hello4() {
		return "hello4";
	}

}
