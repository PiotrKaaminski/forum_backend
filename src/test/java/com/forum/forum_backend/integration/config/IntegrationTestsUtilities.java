package com.forum.forum_backend.integration.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forum.forum_backend.dtos.UserDto;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Component
@Profile("integrationTest")
public class IntegrationTestsUtilities {

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	public String getJwt(String username, String password) throws Exception {
		UserDto userDto = new UserDto() {{
			setUsername(username);
			setPassword(password);
		}};
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectAsJson(userDto)))

				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		String responseAsString = mvcResult.getResponse().getContentAsString();
		UserDto returnedUser =  objectMapper.readValue(responseAsString, UserDto.class);

		Assertions.assertNotNull(returnedUser);
		Assertions.assertEquals(username, returnedUser.getUsername());
		Assertions.assertNotNull(returnedUser.getJwt());

		return returnedUser.getJwt();
	}

	public String objectAsJson(Object obj) throws Exception {
		return objectMapper.writeValueAsString(obj);
	}
}
