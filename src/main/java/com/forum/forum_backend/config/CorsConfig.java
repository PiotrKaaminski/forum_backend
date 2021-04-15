package com.forum.forum_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

	@Value("${cors.allowedOrigins}")
	private String[] allowedOrigins;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		System.out.println(Arrays.toString(allowedOrigins));
		registry.addMapping("/**")
				.allowedMethods("*")
				.allowedOrigins(allowedOrigins)
				.allowedHeaders("*")
				.allowCredentials(true);

	}
}
