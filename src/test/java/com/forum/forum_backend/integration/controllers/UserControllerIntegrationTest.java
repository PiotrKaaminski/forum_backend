package com.forum.forum_backend.integration.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forum.forum_backend.dtos.ExceptionDto;
import com.forum.forum_backend.dtos.PaginatedResponse;
import com.forum.forum_backend.dtos.PermissionDto;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.enums.Permission;
import com.forum.forum_backend.integration.config.IntegrationTestsUtilities;
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

import java.util.Arrays;
import java.util.Collections;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integrationTest")
public class UserControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private IntegrationTestsUtilities testUtils;

	@Test
	@DisplayName("test getUsers with pagination parameters and starting username")
	public void getUsersWithPaginationAndUsername() throws Exception {
		String jwt = testUtils.getJwt("admin1", "admin1pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
				.queryParam("username", "use")
				.queryParam("size", "4")
				.queryParam("page", "1")
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		PaginatedResponse<UserDto> returnedUsers = objectMapper.readValue(
				mvcResult.getResponse().getContentAsString(),
				new TypeReference<>() {}
				);

		Assertions.assertEquals(9, returnedUsers.getCount());
		Assertions.assertEquals(4, returnedUsers.getResults().size());

		Assertions.assertEquals("user5", returnedUsers.getResults().get(0).getUsername());
		Assertions.assertEquals("user@forum.com", returnedUsers.getResults().get(0).getEmail());
		Assertions.assertEquals(Permission.ADMIN, returnedUsers.getResults().get(0).getAuthority().getName());
		Assertions.assertEquals(0, returnedUsers.getResults().get(0).getAuthority().getForumIdList().size());

		Assertions.assertEquals("user6", returnedUsers.getResults().get(1).getUsername());
		Assertions.assertEquals("user@forum.com", returnedUsers.getResults().get(1).getEmail());
		Assertions.assertEquals(Permission.HEAD_MODERATOR, returnedUsers.getResults().get(1).getAuthority().getName());
		Assertions.assertEquals(0, returnedUsers.getResults().get(1).getAuthority().getForumIdList().size());

		Assertions.assertEquals("user7", returnedUsers.getResults().get(2).getUsername());
		Assertions.assertEquals("user@forum.com", returnedUsers.getResults().get(2).getEmail());
		Assertions.assertEquals(Permission.MODERATOR, returnedUsers.getResults().get(2).getAuthority().getName());
		Assertions.assertEquals(2, returnedUsers.getResults().get(2).getAuthority().getForumIdList().size());
		Assertions.assertEquals(1, returnedUsers.getResults().get(2).getAuthority().getForumIdList().get(0));
		Assertions.assertEquals(6, returnedUsers.getResults().get(2).getAuthority().getForumIdList().get(1));

		Assertions.assertEquals("user8", returnedUsers.getResults().get(3).getUsername());
		Assertions.assertEquals("user@forum.com", returnedUsers.getResults().get(3).getEmail());
		Assertions.assertNull(returnedUsers.getResults().get(3).getAuthority().getName());
		Assertions.assertEquals(0, returnedUsers.getResults().get(3).getAuthority().getForumIdList().size());

	}

	@Test
	@DisplayName("test getUsers unauthorized")
	public void getUsersUnauthorized() throws Exception {
		String jwt = testUtils.getJwt("user1", "user1pass");

		mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
				.header("Authorization", "Bearer " + jwt)
				.queryParam("username", "use")
				.queryParam("size", "4")
				.queryParam("page", "1"))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.status().reason("Forbidden"));
	}

	@Test
	@DisplayName("test getUsers unauthenticated")
	public void getUsersUnauthenticated() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
				.queryParam("username", "use")
				.queryParam("size", "4")
				.queryParam("page", "1"))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andExpect(MockMvcResultMatchers.status().reason("Full authentication is required to access this resource"));
	}

	@Test
	@DisplayName("test myAccountInfo")
	public void myAccountInfoSuccess() throws Exception {
		String jwt = testUtils.getJwt("moderator1", "moderator1pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/me")
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		UserDto returnedUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

		Assertions.assertNotNull(returnedUser);
		Assertions.assertEquals(7, returnedUser.getId());
		Assertions.assertEquals("moderator1", returnedUser.getUsername());
		Assertions.assertEquals("user@forum.com", returnedUser.getEmail());

		Assertions.assertNotNull(returnedUser.getAuthority());
		PermissionDto permission = returnedUser.getAuthority();

		Assertions.assertEquals(Permission.MODERATOR, permission.getName());
		Assertions.assertEquals(2, permission.getForumIdList().size());
		Assertions.assertEquals(1, permission.getForumIdList().get(0));
		Assertions.assertEquals(6, permission.getForumIdList().get(1));
	}

	@Test
	@DisplayName("test myAccountInfo unauthenticated")
	public void myAccountInfoUnauthenticated() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/users/me"))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andExpect(MockMvcResultMatchers.status().reason("Full authentication is required to access this resource"));
	}

	@Test
	@DisplayName("test getUser success")
	public void getUserSuccess() throws Exception {
		String jwt = testUtils.getJwt("admin1", "admin1pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/10")
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		UserDto returnedUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

		Assertions.assertNotNull(returnedUser);
		Assertions.assertEquals(10, returnedUser.getId());
		Assertions.assertEquals("user4", returnedUser.getUsername());
		Assertions.assertEquals("user@forum.com", returnedUser.getEmail());

		Assertions.assertNotNull(returnedUser.getAuthority());
		Assertions.assertNull(returnedUser.getAuthority().getName());
		Assertions.assertEquals(0, returnedUser.getAuthority().getForumIdList().size());
	}

	@Test
	@DisplayName("Test getUser not found")
	public void getUserNotFound() throws Exception {
		String jwt = testUtils.getJwt("admin1", "admin1pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/100")
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(404, returnedException.getStatus());
		Assertions.assertEquals("NOT_FOUND", returnedException.getError());
		Assertions.assertEquals("User with id = 100 doesn't exist", returnedException.getMessage());
		Assertions.assertEquals("/api/users/100", returnedException.getPath());
	}

	@Test
	@DisplayName("test getUser unauthorized")
	public void getUserUnauthorized() throws Exception {
		String jwt = testUtils.getJwt("user1", "user1pass");

		mockMvc.perform(MockMvcRequestBuilders.get("/api/users/10")
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.status().reason("Forbidden"));
	}

	@Test
	@DisplayName("test getUser unauthenticated")
	public void getUserUnauthenticated() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/users/10"))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andExpect(MockMvcResultMatchers.status().reason("Full authentication is required to access this resource"));
	}

	@Test
	@DisplayName("test addUser success")
	public void addUserSuccess() throws Exception {
		UserDto userToAdd = new UserDto() {{
			setUsername("addedUser");
			setPassword("addedUserpass");
		}};

		mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(userToAdd)))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		String jwt = testUtils.getJwt("addedUser", "addedUserpass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/me")
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		UserDto returnedUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

		Assertions.assertNotNull(returnedUser);
		Assertions.assertEquals(19, returnedUser.getId());
		Assertions.assertEquals("addedUser", returnedUser.getUsername());
		Assertions.assertEquals("user@forum.com", returnedUser.getEmail());

		Assertions.assertNotNull(returnedUser.getAuthority());
		Assertions.assertNull(returnedUser.getAuthority().getName());
		Assertions.assertEquals(0, returnedUser.getAuthority().getForumIdList().size());
	}

	@Test
	@DisplayName("test addUser username with space")
	public void addUserUsernameWithSpace() throws Exception {
		UserDto userToAdd = new UserDto(){{
			setUsername("userTo Add");
			setPassword("password");
		}};

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
		.contentType(MediaType.APPLICATION_JSON)
		.content(testUtils.objectAsJson(userToAdd)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(400, returnedException.getStatus());
		Assertions.assertEquals("BAD_REQUEST", returnedException.getError());
		Assertions.assertEquals("Do not use space in username and password", returnedException.getMessage());
		Assertions.assertEquals("/api/users", returnedException.getPath());
	}

	@Test
	@DisplayName("test addUser with null username")
	public void addUserWithNullUsername() throws Exception {
		UserDto userToAdd = new UserDto(){{
			setPassword("password");
		}};

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(userToAdd)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(400, returnedException.getStatus());
		Assertions.assertEquals("BAD_REQUEST", returnedException.getError());
		Assertions.assertEquals("Do not use space in username and password", returnedException.getMessage());
		Assertions.assertEquals("/api/users", returnedException.getPath());
	}

	@Test
	@DisplayName("test addUser username with invalid length")
	public void addUserUsernameWithInvalidLength() throws Exception {
		UserDto userToAdd = new UserDto(){{
			setUsername("abc");
			setPassword("password");
		}};

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(userToAdd)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(400, returnedException.getStatus());
		Assertions.assertEquals("BAD_REQUEST", returnedException.getError());
		Assertions.assertEquals("Username cannot be shorter than 5 and longer than 20", returnedException.getMessage());
		Assertions.assertEquals("/api/users", returnedException.getPath());
	}

	@Test
	@DisplayName("test addUser username already used")
	public void addUserUsernameAlreadyUsed() throws Exception {
		UserDto userToAdd = new UserDto(){{
			setUsername("user1");
			setPassword("password");
		}};

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(userToAdd)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(400, returnedException.getStatus());
		Assertions.assertEquals("BAD_REQUEST", returnedException.getError());
		Assertions.assertEquals("User with given username already exists", returnedException.getMessage());
		Assertions.assertEquals("/api/users", returnedException.getPath());
	}

	@Test
	@DisplayName("test addUser password with space")
	public void addUserPasswordWithSpace() throws Exception {
		UserDto userToAdd = new UserDto(){{
			setUsername("testAddingUser");
			setPassword("pass word");
		}};

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(userToAdd)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(400, returnedException.getStatus());
		Assertions.assertEquals("BAD_REQUEST", returnedException.getError());
		Assertions.assertEquals("Do not use space in username and password", returnedException.getMessage());
		Assertions.assertEquals("/api/users", returnedException.getPath());
	}

	@Test
	@DisplayName("test addUser with null password")
	public void addUserWithNullPassword() throws Exception {
		UserDto userToAdd = new UserDto(){{
			setUsername("testAddingUser");
		}};

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(userToAdd)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(400, returnedException.getStatus());
		Assertions.assertEquals("BAD_REQUEST", returnedException.getError());
		Assertions.assertEquals("Do not use space in username and password", returnedException.getMessage());
		Assertions.assertEquals("/api/users", returnedException.getPath());
	}

	@Test
	@DisplayName("test addUser password with invalid length")
	public void addUserPasswordWithInvalidLength() throws Exception {
		UserDto userToAdd = new UserDto(){{
			setUsername("testAddingUser");
			setPassword("abc");
		}};

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(userToAdd)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(400, returnedException.getStatus());
		Assertions.assertEquals("BAD_REQUEST", returnedException.getError());
		Assertions.assertEquals("Password cannot be shorter than 5 and longer than 25", returnedException.getMessage());
		Assertions.assertEquals("/api/users", returnedException.getPath());
	}

	@Test
	@DisplayName("test assignPermission assign admin")
	public void assignPermissionAssignAdmin() throws Exception {
		String jwt = testUtils.getJwt("admin1", "admin1pass");

		PermissionDto permissionToAssign = new PermissionDto() {{
			setName(Permission.ADMIN);
		}};

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/1/permissions")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(permissionToAssign)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		UserDto returnedUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);
		Assertions.assertNotNull(returnedUser);
		Assertions.assertEquals(1, returnedUser.getId());
		Assertions.assertEquals("user1", returnedUser.getUsername());
		Assertions.assertEquals(Permission.ADMIN, returnedUser.getAuthority().getName());
		Assertions.assertEquals(0, returnedUser.getAuthority().getForumIdList().size());
	}

	@Test
	@DisplayName("test assignPermission assign moderator")
	public void assignPermissionAssignModerator() throws Exception {
		String jwt = testUtils.getJwt("admin1", "admin1pass");

		PermissionDto permissionToAssign = new PermissionDto() {{
			setName(Permission.MODERATOR);
			setForumIdList(Arrays.asList(1, 6));
		}};

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/15/permissions")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(permissionToAssign)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		UserDto returnedUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

		Assertions.assertNotNull(returnedUser);
		Assertions.assertEquals(15, returnedUser.getId());
		Assertions.assertEquals("user9", returnedUser.getUsername());
		Assertions.assertEquals(Permission.MODERATOR, returnedUser.getAuthority().getName());
		Assertions.assertEquals(2, returnedUser.getAuthority().getForumIdList().size());
		Assertions.assertEquals(1, returnedUser.getAuthority().getForumIdList().get(0));
		Assertions.assertEquals(6, returnedUser.getAuthority().getForumIdList().get(1));

	}

	@Test
	@DisplayName("test assignPermission revoke HEAD_MODERATOR")
	public void assignPermissionRevokeHeadModerator() throws Exception {
		String jwt = testUtils.getJwt("admin1", "admin1pass");

		PermissionDto permissionToAssign = new PermissionDto();

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/16/permissions")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(permissionToAssign)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		UserDto returnedUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

		Assertions.assertNotNull(returnedUser);
		Assertions.assertEquals(16, returnedUser.getId());
		Assertions.assertEquals("headModerator2", returnedUser.getUsername());
		Assertions.assertNull(returnedUser.getAuthority().getName());
		Assertions.assertEquals(0, returnedUser.getAuthority().getForumIdList().size());

	}

	@Test
	@DisplayName("test assignPermission revoke MODERATOR")
	public void assignPermissionRevokeModerator() throws Exception {
		String jwt = testUtils.getJwt("admin1", "admin1pass");

		PermissionDto permissionToAssign = new PermissionDto();

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/8/permissions")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(permissionToAssign)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		UserDto returnedUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

		Assertions.assertNotNull(returnedUser);
		Assertions.assertEquals(8, returnedUser.getId());
		Assertions.assertEquals("moderator2", returnedUser.getUsername());
		Assertions.assertNull(returnedUser.getAuthority().getName());
		Assertions.assertEquals(0, returnedUser.getAuthority().getForumIdList().size());

	}

	@Test
	@DisplayName("test assignPermission assigning permission to oneself")
	public void assignPermissionAssignPermissionToOneself() throws Exception {
		String jwt = testUtils.getJwt("headModerator1", "headModerator1pass");

		PermissionDto permissionToAssign = new PermissionDto() {{
			setName(Permission.MODERATOR);
			setForumIdList(Arrays.asList(1, 2));
		}};

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/6/permissions")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(permissionToAssign)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(400, returnedException.getStatus());
		Assertions.assertEquals("BAD_REQUEST", returnedException.getError());
		Assertions.assertEquals("You cannot modify your own permissions", returnedException.getMessage());
		Assertions.assertEquals("/api/users/6/permissions", returnedException.getPath());

	}

	@Test
	@DisplayName("test assignPermission user not found")
	public void assignPermissionUserNotFound() throws Exception {
		String jwt = testUtils.getJwt("headModerator1", "headModerator1pass");

		PermissionDto permissionToAssign = new PermissionDto() {{
			setName(Permission.MODERATOR);
			setForumIdList(Arrays.asList(1, 2));
		}};

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/100/permissions")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(permissionToAssign)))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(404, returnedException.getStatus());
		Assertions.assertEquals("NOT_FOUND", returnedException.getError());
		Assertions.assertEquals("User with id = 100 doesn't exist", returnedException.getMessage());
		Assertions.assertEquals("/api/users/100/permissions", returnedException.getPath());

	}

	@Test
	@DisplayName("test assignPermission assign ADMIN without permissions")
	public void assignPermissionAssignAdminWithoutPermissions() throws Exception {
		String jwt = testUtils.getJwt("headModerator1", "headModerator1pass");

		PermissionDto permissionToAssign = new PermissionDto() {{
			setName(Permission.ADMIN);
		}};

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/1/permissions")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(permissionToAssign)))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(403, returnedException.getStatus());
		Assertions.assertEquals("FORBIDDEN", returnedException.getError());
		Assertions.assertEquals("You cannot assign ADMIN permission", returnedException.getMessage());
		Assertions.assertEquals("/api/users/1/permissions", returnedException.getPath());

	}

	@Test
	@DisplayName("test assignPermission assign MODERATOR with empty forumIdList")
	public void assignPermissionAssignModeratorWithEmptyForumIdList() throws Exception {
		String jwt = testUtils.getJwt("headModerator1", "headModerator1pass");

		PermissionDto permissionToAssign = new PermissionDto() {{
			setName(Permission.MODERATOR);
			setForumIdList(Collections.emptyList());
		}};

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/1/permissions")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(permissionToAssign)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(400, returnedException.getStatus());
		Assertions.assertEquals("BAD_REQUEST", returnedException.getError());
		Assertions.assertEquals("Forum id list cannot be empty when assigning MODERATOR permission", returnedException.getMessage());
		Assertions.assertEquals("/api/users/1/permissions", returnedException.getPath());

	}

	@Test
	@DisplayName("test assignPermission degrade ADMIN to MODERATOR without permission")
	public void assignPermissionDegradeAdminToModeratorWithoutPersmission() throws Exception {
		String jwt = testUtils.getJwt("headModerator1", "headModerator1pass");

		PermissionDto permissionToAssign = new PermissionDto() {{
			setName(Permission.MODERATOR);
			setForumIdList(Arrays.asList(1, 2));
		}};

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/4/permissions")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(permissionToAssign)))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(403, returnedException.getStatus());
		Assertions.assertEquals("FORBIDDEN", returnedException.getError());
		Assertions.assertEquals("You cannot degrade user with id = 4 from ADMIN to MODERATOR", returnedException.getMessage());
		Assertions.assertEquals("/api/users/4/permissions", returnedException.getPath());

	}

	@Test
	@DisplayName("test assignPermission assign MODERATOR to non existing forum")
	public void assignPermissionAssignModeratorToNonExistingForum() throws Exception {
		String jwt = testUtils.getJwt("headModerator1", "headModerator1pass");

		PermissionDto permissionToAssign = new PermissionDto() {{
			setName(Permission.MODERATOR);
			setForumIdList(Arrays.asList(1, 100));
		}};

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/1/permissions")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(permissionToAssign)))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(404, returnedException.getStatus());
		Assertions.assertEquals("NOT_FOUND", returnedException.getError());
		Assertions.assertEquals("Forum with id = 100 doesn't exist", returnedException.getMessage());
		Assertions.assertEquals("/api/users/1/permissions", returnedException.getPath());

	}

	@Test
	@DisplayName("test assignPermission assign MODERATOR to forum without permission to moderate")
	public void assignPermissionAssignModeratorToForumWithoutPermissionToModerate() throws Exception {
		String jwt = testUtils.getJwt("moderator1", "moderator1pass");

		PermissionDto permissionToAssign = new PermissionDto() {{
			setName(Permission.MODERATOR);
			setForumIdList(Collections.singletonList(1));
		}};

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/1/permissions")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(permissionToAssign)))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(403, returnedException.getStatus());
		Assertions.assertEquals("FORBIDDEN", returnedException.getError());
		Assertions.assertEquals("You have no permission to assign moderator to forum with id = 1", returnedException.getMessage());
		Assertions.assertEquals("/api/users/1/permissions", returnedException.getPath());

	}

	@Test
	@DisplayName("test assignPermission revoke moderator from forum without permission")
	public void assignPermissionRevokeModeratorFromForumWithoutPermission() throws Exception {
		String jwt = testUtils.getJwt("moderator1", "moderator1pass");

		PermissionDto permissionToAssign = new PermissionDto() {{
			setName(Permission.MODERATOR);
			setForumIdList(Collections.singletonList(3));
		}};

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/18/permissions")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(permissionToAssign)))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(403, returnedException.getStatus());
		Assertions.assertEquals("FORBIDDEN", returnedException.getError());
		Assertions.assertEquals("You have no permission to revoke moderator from forum with id = 2", returnedException.getMessage());
		Assertions.assertEquals("/api/users/18/permissions", returnedException.getPath());

	}

	@Test
	@DisplayName("test assignPermission revoke ADMIN without permission")
	public void assignPermissionRevokeAdminWithoutPermission() throws Exception {
		String jwt = testUtils.getJwt("moderator1", "moderator1pass");

		PermissionDto permissionToAssign = new PermissionDto();

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/4/permissions")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(permissionToAssign)))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(403, returnedException.getStatus());
		Assertions.assertEquals("FORBIDDEN", returnedException.getError());
		Assertions.assertEquals("You cannot revoke ADMIN permission", returnedException.getMessage());
		Assertions.assertEquals("/api/users/4/permissions", returnedException.getPath());

	}

	@Test
	@DisplayName("test assignPermission revoke MODERATOR without permission")
	public void assignPermissionRevokeMODERATORWithoutPermission() throws Exception {
		String jwt = testUtils.getJwt("moderator1", "moderator1pass");

		PermissionDto permissionToAssign = new PermissionDto();

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/18/permissions")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(permissionToAssign)))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(403, returnedException.getStatus());
		Assertions.assertEquals("FORBIDDEN", returnedException.getError());
		Assertions.assertEquals("You have no permission to revoke MODERATOR from forum with id = 2", returnedException.getMessage());
		Assertions.assertEquals("/api/users/18/permissions", returnedException.getPath());

	}

}

















