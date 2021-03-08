package com.forum.forum_backend.controllers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class ThreadControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private AuthenticationController authenticationController;

	@Test
	public void testGetThreadSuccess() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/threads/1"))
				// validate the response code and content type
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))

				// validate returned fields
				.andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.canModerate", Matchers.is(false)));

	}


}
