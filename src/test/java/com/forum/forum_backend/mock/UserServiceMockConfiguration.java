package com.forum.forum_backend.mock;

import com.forum.forum_backend.services.interfaces.UserService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@ActiveProfiles("unitTest")
public class UserServiceMockConfiguration {

	@Bean
	@Primary
	public UserService userService() {
		return Mockito.mock(UserService.class);
	}

}
