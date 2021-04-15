package com.forum.forum_backend.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forum.forum_backend.dtos.*;
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

	@Test
	@DisplayName("Test getThread as thread author")
	public void getThreadSuccessAsAuthor() throws Exception {
		String jwt = testUtils.getJwt("user3", "user3pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/threads/6")
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ThreadDto returnedThread = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ThreadDto.class);

		Assertions.assertNotNull(returnedThread);
		Assertions.assertEquals(6, returnedThread.getId());
		Assertions.assertEquals("thread 6 title", returnedThread.getTitle());
		Assertions.assertEquals("thread message", returnedThread.getMessage());
		Assertions.assertEquals(0, returnedThread.getLikesAmount());
		Assertions.assertTrue(returnedThread.isCanModerate());
		Assertions.assertTrue(returnedThread.isLocked());
		Assertions.assertFalse(returnedThread.isLiked());

		Assertions.assertEquals(2, returnedThread.getBreadcrumb().size());
		List<ForumDto> breadcrumb = returnedThread.getBreadcrumb();

		Assertions.assertEquals(2, breadcrumb.get(0).getId());
		Assertions.assertEquals("main forum 2", breadcrumb.get(0).getTitle());
		Assertions.assertEquals(6, breadcrumb.get(1).getId());
		Assertions.assertEquals("sub forum 1 in 2", breadcrumb.get(1).getTitle());

		Assertions.assertNotNull(returnedThread.getPosts());
		Assertions.assertEquals(1, returnedThread.getPosts().getCount());
		Assertions.assertEquals("post 15 message", returnedThread.getPosts().getResults().get(0).getMessage());
		Assertions.assertEquals(returnedThread.getId(), returnedThread.getPosts().getResults().get(0).getThreadId());
		Assertions.assertFalse(returnedThread.getPosts().getResults().get(0).isCanModerate());
	}

	@Test
	@DisplayName("Test getThread as moderator")
	public void getThreadsSuccessAsModerator() throws Exception {
		String jwt = testUtils.getJwt("admin1", "admin1pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/threads/2")
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ThreadDto returnedThread = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ThreadDto.class);

		Assertions.assertNotNull(returnedThread);
		Assertions.assertEquals(2, returnedThread.getId());
		Assertions.assertEquals("thread 2 title", returnedThread.getTitle());
		Assertions.assertEquals("thread message", returnedThread.getMessage());
		Assertions.assertEquals(4, returnedThread.getLikesAmount());
		Assertions.assertTrue(returnedThread.isCanModerate());
		Assertions.assertTrue(returnedThread.isLocked());
		Assertions.assertTrue(returnedThread.isLiked());

		Assertions.assertEquals(3, returnedThread.getPosts().getCount());
		Assertions.assertEquals(3, returnedThread.getPosts().getResults().size());

		for (PostDto post : returnedThread.getPosts().getResults()) {
			Assertions.assertEquals(2, post.getThreadId());
			Assertions.assertTrue(post.isCanModerate());
		}
	}

	@Test
	@DisplayName("Test getThread with pagination parameters")
	public void getThreadsSuccessWithPaginationParameters() throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/threads/1")
				.queryParam("size", "3")
				.queryParam("page", "1"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ThreadDto returnedThread = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ThreadDto.class);

		Assertions.assertNotNull(returnedThread);
		Assertions.assertEquals(1, returnedThread.getId());
		Assertions.assertEquals("thread 1 title", returnedThread.getTitle());
		Assertions.assertEquals("thread message", returnedThread.getMessage());
		Assertions.assertEquals(6, returnedThread.getLikesAmount());
		Assertions.assertFalse(returnedThread.isLocked());
		Assertions.assertFalse(returnedThread.isCanModerate());
		Assertions.assertFalse(returnedThread.isLiked());

		Assertions.assertNotNull(returnedThread.getPosts().getResults());
		Assertions.assertEquals(8, returnedThread.getPosts().getCount());
		Assertions.assertEquals(3, returnedThread.getPosts().getResults().size());

		List<PostDto> posts = returnedThread.getPosts().getResults();
		for (PostDto post : posts) {
			Assertions.assertEquals(1, post.getThreadId());
		}
		Assertions.assertEquals("post 4 message", posts.get(0).getMessage());
		Assertions.assertEquals("post 16 message", posts.get(1).getMessage());
		Assertions.assertEquals("post 17 message", posts.get(2).getMessage());
	}

	@Test
	@DisplayName("Test getThread not found")
	public void getThreadNotFound() throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/threads/100"))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(404, returnedException.getStatus());
		Assertions.assertEquals("NOT_FOUND", returnedException.getError());
		Assertions.assertEquals("Thread with id = 100 doesn't exist", returnedException.getMessage());
		Assertions.assertEquals("/api/threads/100", returnedException.getPath());
	}

	@Test
	@DisplayName("Test addThread Success")
	public void addThreadSuccess() throws Exception {
		ThreadDto threadToAdd = new ThreadDto() {{
			setTitle("title");
			setMessage("message");
			setForum(new ForumDto() {{
				setId(3);
			}});
		}};

		String jwt = testUtils.getJwt("user1", "user1pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/threads")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(threadToAdd)))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ThreadDto returnedThread = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ThreadDto.class);
		Assertions.assertNotNull(returnedThread);
		Assertions.assertEquals(7, returnedThread.getId());
		Assertions.assertEquals("title", returnedThread.getTitle());
		Assertions.assertEquals("message", returnedThread.getMessage());
		Assertions.assertEquals(0, returnedThread.getLikesAmount());
		Assertions.assertTrue(returnedThread.isCanModerate());
		Assertions.assertFalse(returnedThread.isLocked());
		Assertions.assertFalse(returnedThread.isLiked());

		Assertions.assertEquals(2, returnedThread.getBreadcrumb().size());
		List<ForumDto> breadcrumb = returnedThread.getBreadcrumb();
		Assertions.assertEquals(1, breadcrumb.get(0).getId());
		Assertions.assertEquals("main forum 1", breadcrumb.get(0).getTitle());
		Assertions.assertEquals(3, breadcrumb.get(1).getId());
		Assertions.assertEquals("sub forum 1 in 1", breadcrumb.get(1).getTitle());

	}

	@Test
	@DisplayName("Test addThread unauthenticated")
	public void addThreadUnauthenticated() throws Exception {
		ThreadDto threadToAdd = new ThreadDto() {{
			setTitle("title");
			setMessage("message");
			setForum(new ForumDto() {{
				setId(3);
			}});
		}};

		mockMvc.perform(MockMvcRequestBuilders.post("/api/threads")
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(threadToAdd)))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andExpect(MockMvcResultMatchers.status().reason("Full authentication is required to access this resource"));

	}

	@Test
	@DisplayName("Test addThread parent forum not found")
	public void addThreadParentForumNotfound() throws Exception {
		ThreadDto threadToAdd = new ThreadDto() {{
			setTitle("title");
			setMessage("message");
			setForum(new ForumDto() {{
				setId(100);
			}});
		}};

		String jwt = testUtils.getJwt("user1", "user1pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/threads")
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(threadToAdd))
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(404, returnedException.getStatus());
		Assertions.assertEquals("NOT_FOUND", returnedException.getError());
		Assertions.assertEquals("Forum with id = 100 doesn't exist", returnedException.getMessage());
		Assertions.assertEquals("/api/threads", returnedException.getPath());
	}

	@Test
	@DisplayName("Test addThread adding to main forum refused")
	public void addThreadToMainForumRefused() throws Exception {
		ThreadDto threadToAdd = new ThreadDto() {{
			setTitle("title");
			setMessage("message");
			setForum(new ForumDto() {{
				setId(1);
			}});
		}};

		String jwt = testUtils.getJwt("user1", "user1pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/threads")
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(threadToAdd))
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(403, returnedException.getStatus());
		Assertions.assertEquals("FORBIDDEN", returnedException.getError());
		Assertions.assertEquals("Cannot add thread to main forum", returnedException.getMessage());
		Assertions.assertEquals("/api/threads", returnedException.getPath());
	}

	@Test
	@DisplayName("Test addThread with empty title refused")
	public void addThreadWithInvalidTitle() throws Exception {
		ThreadDto threadToAdd = new ThreadDto() {{
			setMessage("message");
			setForum(new ForumDto() {{
				setId(1);
			}});
		}};

		String jwt = testUtils.getJwt("user1", "user1pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/threads")
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(threadToAdd))
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(400, returnedException.getStatus());
		Assertions.assertEquals("BAD_REQUEST", returnedException.getError());
		Assertions.assertEquals("Title cannot be empty", returnedException.getMessage());
		Assertions.assertEquals("/api/threads", returnedException.getPath());
	}

	@Test
	@DisplayName("Test addThread with empty message refused")
	public void addThreadWithInvalidMessage() throws Exception {
		ThreadDto threadToAdd = new ThreadDto() {{
			setTitle("title");
			setForum(new ForumDto() {{
				setId(1);
			}});
		}};

		String jwt = testUtils.getJwt("user1", "user1pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/threads")
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(threadToAdd))
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(400, returnedException.getStatus());
		Assertions.assertEquals("BAD_REQUEST", returnedException.getError());
		Assertions.assertEquals("Message cannot be empty", returnedException.getMessage());
		Assertions.assertEquals("/api/threads", returnedException.getPath());
	}

	@Test
	@DisplayName("Test addThread with empty parent forum refused")
	public void addThreadWithInvalidParent() throws Exception {
		ThreadDto threadToAdd = new ThreadDto() {{
			setTitle("title");
			setMessage("message");
		}};

		String jwt = testUtils.getJwt("user1", "user1pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/threads")
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(threadToAdd))
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(400, returnedException.getStatus());
		Assertions.assertEquals("BAD_REQUEST", returnedException.getError());
		Assertions.assertEquals("Parent forum cannot be empty", returnedException.getMessage());
		Assertions.assertEquals("/api/threads", returnedException.getPath());
	}

	@Test
	@DisplayName("Test deleteThread success")
	public void deleteThreadSuccess() throws Exception {
		String jwt = testUtils.getJwt("admin1", "admin1pass");

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/threads/5")
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DisplayName("Test deleteThread unauthenticated")
	public void deleteThreadUnauthenticated() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/threads/1"))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andExpect(MockMvcResultMatchers.status().reason("Full authentication is required to access this resource"));

	}

	@Test
	@DisplayName("Test deleteThread not found")
	public void deleteThreadNotFound() throws Exception {
		String jwt = testUtils.getJwt("user1", "user1pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/threads/100")
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(404, returnedException.getStatus());
		Assertions.assertEquals("NOT_FOUND", returnedException.getError());
		Assertions.assertEquals("Thread with id = 100 doesn't exist", returnedException.getMessage());
		Assertions.assertEquals("/api/threads/100", returnedException.getPath());
	}

	@Test
	@DisplayName("Test deleteThread unauthorized")
	public void deleteThreadUnauthorized() throws Exception {
		String jwt = testUtils.getJwt("user3", "user3pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/threads/2")
				.header("Authorization", "Bearer " + jwt))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(403, returnedException.getStatus());
		Assertions.assertEquals("FORBIDDEN", returnedException.getError());
		Assertions.assertEquals("You have no permissions to delete this thread", returnedException.getMessage());
		Assertions.assertEquals("/api/threads/2", returnedException.getPath());
	}

	@Test
	@DisplayName("Test modifyThread set liked success")
	public void modifyThreadSetLikedSuccess() throws Exception {
		ThreadDto dataToModify = new ThreadDto() {{
			setLiked(true);
		}};

		String jwt = testUtils.getJwt("user1", "user1pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/threads/4")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(dataToModify)))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		ThreadDto returnedThread = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ThreadDto.class);

		Assertions.assertTrue(returnedThread.isLiked());
		Assertions.assertEquals(4, returnedThread.getLikesAmount());
	}

	@Test
	@DisplayName("Test modifyThread set locked success")
	public void modifyThreadSetLockedSuccess() throws Exception {
		ThreadDto dataToModify = new ThreadDto() {{
			setLocked(true);
		}};

		String jwt = testUtils.getJwt("user2", "user2pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/threads/4")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(dataToModify)))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		ThreadDto returnedThread = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ThreadDto.class);

		Assertions.assertTrue(returnedThread.isLocked());
	}

	@Test
	@DisplayName("Test modifyThread unauthenticated")
	public void modifyThreadUnauthenticated() throws Exception {
		ThreadDto dataToModify = new ThreadDto() {{
			setLiked(true);
			setLocked(true);
		}};

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/threads/4")
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(dataToModify)))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andExpect(MockMvcResultMatchers.status().reason("Full authentication is required to access this resource"));

	}

	@Test
	@DisplayName("Test modifyThread not found")
	public void modifyThreadNotFound() throws Exception {
		ThreadDto dataToModify = new ThreadDto() {{
			setLiked(true);
			setLocked(true);
		}};

		String jwt = testUtils.getJwt("admin1", "admin1pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/threads/100")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(dataToModify)))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(404, returnedException.getStatus());
		Assertions.assertEquals("NOT_FOUND", returnedException.getError());
		Assertions.assertEquals("Thread with id = 100 doesn't exist", returnedException.getMessage());
		Assertions.assertEquals("/api/threads/100", returnedException.getPath());
	}

	@Test
	@DisplayName("Test modifyThread unauthorized")
	public void modifyThreadUnauthorized() throws Exception {
		ThreadDto dataToModify = new ThreadDto() {{
			setLiked(true);
			setLocked(true);
		}};

		String jwt = testUtils.getJwt("user3", "user3pass");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/threads/4")
				.header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(testUtils.objectAsJson(dataToModify)))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		ExceptionDto returnedException = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExceptionDto.class);

		Assertions.assertNotNull(returnedException);
		Assertions.assertEquals(403, returnedException.getStatus());
		Assertions.assertEquals("FORBIDDEN", returnedException.getError());
		Assertions.assertEquals("You have no permissions to modify this thread", returnedException.getMessage());
		Assertions.assertEquals("/api/threads/4", returnedException.getPath());
	}

}
