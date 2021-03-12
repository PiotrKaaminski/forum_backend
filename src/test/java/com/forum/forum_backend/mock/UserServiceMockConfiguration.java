package com.forum.forum_backend.mock;

import com.forum.forum_backend.repositories.UserRepository;
import com.forum.forum_backend.services.UserServiceImpl;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@Profile("unitTest")
public class UserServiceMockConfiguration {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Bean
	@Primary
	public UserService userService() {
		return new UserServiceImpl(userRepository, bCryptPasswordEncoder);
	}

}
