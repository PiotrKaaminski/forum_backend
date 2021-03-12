package com.forum.forum_backend.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forum.forum_backend.dtos.ForumDto;
import com.forum.forum_backend.dtos.PostDto;
import com.forum.forum_backend.dtos.ThreadDto;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.integration.IntegrationTestsUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integrationTest")
public class ThreadControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private IntegrationTestsUtilities testUtils;

	@Test
	@DisplayName("Test getThread without having permissions to modify with default size and page values")
	public void testGetThreadSuccessUnauthorized() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/threads/1"))
				// validate the response code and content type
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ThreadDto returnedThread = objectMapper.readValue(result.getResponse().getContentAsString(), ThreadDto.class);

		Assertions.assertNotNull(returnedThread);
		Assertions.assertEquals(1, returnedThread.getId());
		Assertions.assertEquals("thread 1 title", returnedThread.getTitle());
		Assertions.assertEquals("thread message", returnedThread.getMessage());
		Assertions.assertEquals(6, returnedThread.getLikesAmount());
		Assertions.assertFalse(returnedThread.isLocked());
		Assertions.assertFalse(returnedThread.isCanModerate());
		Assertions.assertFalse(returnedThread.isLiked());

		Assertions.assertNotNull(returnedThread.getCreator());
		UserDto creator = returnedThread.getCreator();
		Assertions.assertEquals(1, creator.getId());
		Assertions.assertEquals("user1", creator.getUsername());

		Assertions.assertNotNull(returnedThread.getBreadcrumb());
		List<ForumDto> breadcrumb = returnedThread.getBreadcrumb();
		Assertions.assertEquals(2, breadcrumb.size());
		Assertions.assertEquals(1, breadcrumb.get(0).getId());
		Assertions.assertEquals("main forum 1", breadcrumb.get(0).getTitle());
		Assertions.assertEquals(3, breadcrumb.get(1).getId());
		Assertions.assertEquals("sub forum 1 in 1", breadcrumb.get(1).getTitle());

		Assertions.assertNotNull(returnedThread.getPosts().getResults());
		Assertions.assertEquals(8, returnedThread.getPosts().getCount());
		List<PostDto> posts = returnedThread.getPosts().getResults();

		for (PostDto post: posts) {
			Assertions.assertEquals(returnedThread.getId(), post.getThreadId());
			Assertions.assertFalse(post.isLiked());
			Assertions.assertFalse(post.isCanModerate());
		}
	}

}
