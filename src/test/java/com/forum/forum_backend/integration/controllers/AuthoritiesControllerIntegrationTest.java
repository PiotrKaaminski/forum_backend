package com.forum.forum_backend.integration.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class AuthoritiesControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private IntegrationTestsUtilities testUtils;

	@Test
	@DisplayName("test getAuthorities as moderator")
	public void getAuthoritiesAsModerator() throws Exception {
		String jwt = testUtils.getJwt("moderator1", "moderator1pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/authorities")
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		List<String> returnedPermissions = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
		});

		Assertions.assertNotNull(returnedPermissions);
		Assertions.assertEquals(1, returnedPermissions.size());
		Assertions.assertEquals("MODERATOR", returnedPermissions.get(0));
	}

	@Test
	@DisplayName("test getAuthorities as head_moderator")
	public void getAuthoritiesAsHeadModerator() throws Exception {
		String jwt = testUtils.getJwt("headModerator1", "headModerator1pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/authorities")
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		List<String> returnedPermissions = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
		});

		Assertions.assertNotNull(returnedPermissions);
		Assertions.assertEquals(1, returnedPermissions.size());
		Assertions.assertEquals("MODERATOR", returnedPermissions.get(0));
	}

	@Test
	@DisplayName("test getAuthorities as admin")
	public void getAuthoritiesAsAdmin() throws Exception {
		String jwt = testUtils.getJwt("admin1", "admin1pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/authorities")
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		List<String> returnedPermissions = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
		});

		Assertions.assertNotNull(returnedPermissions);
		Assertions.assertEquals(3, returnedPermissions.size());
		Assertions.assertEquals("ADMIN", returnedPermissions.get(0));
		Assertions.assertEquals("HEAD_MODERATOR", returnedPermissions.get(1));
		Assertions.assertEquals("MODERATOR", returnedPermissions.get(2));
	}

	@Test
	@DisplayName("test getAuthorities as user")
	public void getAuthoritiesAsUser() throws Exception {
		String jwt = testUtils.getJwt("user1", "user1pass");

		mockMvc.perform(MockMvcRequestBuilders.get("/api/authorities")
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.status().reason("Forbidden"));
	}

	@Test
	@DisplayName("test getAuthorities unauthenticated")
	public void getAuthoritiesUnauthenticated() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/authorities"))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andExpect(MockMvcResultMatchers.status().reason("Full authentication is required to access this resource"));
	}


}
