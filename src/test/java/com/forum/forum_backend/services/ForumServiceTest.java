package com.forum.forum_backend.services;

import com.forum.forum_backend.dtos.ForumDto;
import com.forum.forum_backend.dtos.PaginatedResponse;
import com.forum.forum_backend.dtos.ThreadDto;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.ForumEntity;
import com.forum.forum_backend.models.ThreadEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.repositories.ForumRepository;
import com.forum.forum_backend.services.interfaces.ForumService;
import com.forum.forum_backend.services.interfaces.ThreadService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("unitTest")
public class ForumServiceTest {

	@MockBean
	private ForumRepository forumRepository;
	@MockBean
	private ThreadService threadService;

	@Autowired
	private UserService userService;
	@Autowired
	private ForumService forumService;

	@Test
	@DisplayName("Test getMainForumList success")
	public void testGetMainForumListSuccess() {
		// set up mock scenario
		Timestamp creationTime = Timestamp.valueOf(LocalDateTime.of(2000, 1, 1, 1, 1));

		ForumEntity childForumEntity1 = new ForumEntity() {{
			setId(2);
			setTitle("Child forum 1 title");
			setDescription("Child forum 1 description");
			setCreateTime(creationTime);
		}};

		ForumEntity parentForumEntity1 = new ForumEntity() {{
			setId(1);
			setTitle("Parent forum 1 title");
			setDescription("Parent forum 1 description");
			setCreateTime(creationTime);
			setParentForum(null);
			setChildForums(Collections.singletonList(childForumEntity1));
		}};

		childForumEntity1.setParentForum(parentForumEntity1);

		Mockito.doReturn(Collections.singletonList(parentForumEntity1)).when(forumRepository).findAllByParentForumId(null);
		Mockito.doReturn(false).when(userService).isUserPermittedToModerate(null);
		Mockito.doReturn(true).when(userService).isUserPermittedToModerate(parentForumEntity1);

		// execute service method
		PaginatedResponse<ForumDto> returnedResponse = forumService.getMainForumList();

		// perform assertions
		Assertions.assertEquals(1, returnedResponse.getCount(), "Forum service should return 1 ForumDto");

		ForumDto returnedForumDto = returnedResponse.getResults().get(0);
		Assertions.assertEquals(1, returnedForumDto.getId());
		Assertions.assertEquals("Parent forum 1 title", returnedForumDto.getTitle());
		Assertions.assertEquals("Parent forum 1 description", returnedForumDto.getDescription());
		Assertions.assertFalse(returnedForumDto.isCanModerate());
		Assertions.assertEquals(creationTime, returnedForumDto.getCreateTime());
		Assertions.assertNull(returnedForumDto.getParent());

		Assertions.assertEquals(1, returnedForumDto.getForums().size(), "Forum should have 1 sub forum");

		ForumDto returnedChildForumDto = returnedForumDto.getForums().get(0);
		Assertions.assertEquals(2, returnedChildForumDto.getId());
		Assertions.assertEquals("Child forum 1 title", returnedChildForumDto.getTitle());
		Assertions.assertEquals("Child forum 1 description", returnedChildForumDto.getDescription());
		Assertions.assertTrue(returnedChildForumDto.isCanModerate());
		Assertions.assertEquals(creationTime, returnedChildForumDto.getCreateTime());
		Assertions.assertNull(returnedChildForumDto.getLatestThread());

	}

	@Test
	@DisplayName("Test getSubForum success")
	public void testGetSubForumSuccess() throws NotFoundException {
		// setup mock scenario
		ForumEntity parentForum1 = new ForumEntity() {{
			setId(1);
			setTitle("parent forum 1 title");
		}};

		ForumEntity parentForum2 = new ForumEntity() {{
			setId(2);
			setTitle("parent forum 2 title");
			setParentForum(parentForum1);
		}};

		Timestamp creationTime = Timestamp.valueOf(LocalDateTime.of(2000, 1, 1, 1, 1));

		ForumEntity childForumEntity = new ForumEntity() {{
			setId(4);
			setTitle("Child forum 1 title");
			setDescription("Child forum 1 description");
			setCreateTime(creationTime);
		}};

		ForumEntity forumToReturn = new ForumEntity() {{
			setId(3);
			setTitle("forum to return title");
			setDescription("forum to return description");
			setCreateTime(creationTime);
			setParentForum(parentForum2);
			setChildForums(Collections.singletonList(childForumEntity));
		}};

		childForumEntity.setParentForum(forumToReturn);

		UserEntity threadAuthor = new UserEntity() {{
			setId(1);
			setUsername("user1");
		}};

		ThreadEntity threadEntity = new ThreadEntity() {{
			setId(1);
			setTitle("thread title");
			setCreateTime(creationTime);
			setLocked(false);
			setUser(threadAuthor);
			setParentForum(forumToReturn);
		}};

		forumToReturn.setThreadEntities(Collections.singletonList(threadEntity));

		ThreadEntity subForumChildThreadEntity = new ThreadEntity() {{
			setId(2);
			setTitle("latest thread title");
			setCreateTime(creationTime);
			setLocked(false);
			setUser(threadAuthor);
			setParentForum(childForumEntity);
		}};

		childForumEntity.setThreadEntities(Collections.singletonList(subForumChildThreadEntity));

		Mockito.doReturn(forumToReturn).when(forumRepository).getOne(3);
		Mockito.doReturn(false).when(userService).isUserPermittedToModerate(Mockito.any());

		ThreadDto threadDto = new ThreadDto() {{
			setId(threadEntity.getId());
			setTitle(threadEntity.getTitle());
			setCreateTime(threadEntity.getCreateTime());
			setCanModerate(false);
			setLocked(false);
			setPostsAmount(0);
			setLikesAmount(0);
			setCreator(new UserDto() {{
				setId(threadAuthor.getId());
				setUsername(threadAuthor.getUsername());
			}});
		}};

		Mockito.doReturn(threadDto).when(threadService).mapChildEntityToDto(threadEntity);

		// execute service method
		ForumDto returnedForum = forumService.getSubForum(3, true);

		// perform assertions

		Assertions.assertNotNull(returnedForum);
		Assertions.assertEquals(3, returnedForum.getId());
		Assertions.assertEquals("forum to return title", returnedForum.getTitle());
		Assertions.assertEquals("forum to return description", returnedForum.getDescription());
		Assertions.assertEquals(creationTime, returnedForum.getCreateTime());
		Assertions.assertFalse(returnedForum.isCanModerate());

		Assertions.assertEquals(3, returnedForum.getBreadcrumb().size());
		List<ForumDto> breadcrumb = returnedForum.getBreadcrumb();

		Assertions.assertEquals(1, breadcrumb.get(0).getId());
		Assertions.assertEquals("parent forum 1 title", breadcrumb.get(0).getTitle());
		Assertions.assertEquals(2, breadcrumb.get(1).getId());
		Assertions.assertEquals("parent forum 2 title", breadcrumb.get(1).getTitle());
		Assertions.assertEquals(3, breadcrumb.get(2).getId());
		Assertions.assertEquals("forum to return title", breadcrumb.get(2).getTitle());

		Assertions.assertNotNull(returnedForum.getParent());
		Assertions.assertEquals(2, returnedForum.getParent().getId());
		Assertions.assertEquals("parent forum 2 title", returnedForum.getParent().getTitle());

		Assertions.assertEquals(1, returnedForum.getForums().size());
		ForumDto childForumDto = returnedForum.getForums().get(0);

		Assertions.assertEquals(4, childForumDto.getId());
		Assertions.assertEquals("Child forum 1 title", childForumDto.getTitle());
		Assertions.assertEquals("Child forum 1 description", childForumDto.getDescription());
		Assertions.assertEquals(creationTime, childForumDto.getCreateTime());
		Assertions.assertFalse(childForumDto.isCanModerate());

		Assertions.assertNotNull(childForumDto.getLatestThread());
		ThreadDto latestThread = childForumDto.getLatestThread();

		Assertions.assertEquals(2, latestThread.getId());
		Assertions.assertEquals("latest thread title", latestThread.getTitle());
		Assertions.assertEquals(creationTime, latestThread.getCreateTime());
		Assertions.assertEquals(1, latestThread.getCreator().getId());
		Assertions.assertEquals("user1", latestThread.getCreator().getUsername());

		Assertions.assertEquals(1, returnedForum.getThreads().size());
		ThreadDto childThread = returnedForum.getThreads().get(0);

		Assertions.assertEquals(1, childThread.getId());
		Assertions.assertEquals("thread title", childThread.getTitle());
		Assertions.assertEquals(creationTime, childThread.getCreateTime());
		Assertions.assertFalse(childThread.isCanModerate());
		Assertions.assertFalse(childThread.isLocked());
		Assertions.assertEquals(0, childThread.getPostsAmount());
		Assertions.assertEquals(0, childThread.getLikesAmount());
		Assertions.assertEquals(1, childThread.getCreator().getId());
		Assertions.assertEquals("user1", childThread.getCreator().getUsername());

	}

	@Test
	@DisplayName("Test getSubForum NotFoundException")
	public void testGetSubForumBadForumId(){
		// setup mock scenario
		Mockito.doThrow(EntityNotFoundException.class).when(forumRepository).getOne(1);

		// retrieve exception from forumService method
		NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> forumService.getSubForum(1, true));

		// perform assertions
		Assertions.assertEquals("Forum with id = 1 doesn't exist", exception.getMessage());
		Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
	}

	@Test
	@DisplayName("Test addForum with no parent success")
	public void testAddForumWithoutParentSuccess() throws NotFoundException, UnauthorizedException {
		// set up mock scenario
		ForumDto forumToAdd = new ForumDto() {{
			setTitle("forum to add title");
			setDescription("forum to add description");
		}};

		Timestamp creationTime = Timestamp.valueOf(LocalDateTime.of(2000, 1, 1, 1, 1));

		ForumEntity createdForumEntity = new ForumEntity() {{
			setId(1);
			setTitle("forum to add title");
			setDescription("forum to add description");
			setCreateTime(creationTime);
		}};

		Mockito.doReturn(true).when(userService).isUserPermittedToModerate(null);
		Mockito.doReturn(createdForumEntity).when(forumRepository).save(Mockito.any());
		Mockito.doReturn(createdForumEntity).when(forumRepository).getOne(1);

		// execute service method
		ForumDto returnedForum = forumService.addForum(forumToAdd);

		// perform assertions
		Assertions.assertNotNull(returnedForum);
		Assertions.assertEquals(1, returnedForum.getId());
		Assertions.assertEquals("forum to add title", returnedForum.getTitle());
		Assertions.assertEquals("forum to add description", returnedForum.getDescription());
		Assertions.assertEquals(creationTime, returnedForum.getCreateTime());
		Assertions.assertEquals(1, returnedForum.getBreadcrumb().size());
		Assertions.assertEquals(0, returnedForum.getForums().size());
		Assertions.assertEquals(0, returnedForum.getThreads().size());
		Assertions.assertTrue(returnedForum.isCanModerate());

	}

	@Test
	@DisplayName("Test addForum with parent forum success")
	public void testAddForumWithParentSuccess() throws NotFoundException, UnauthorizedException {
		// setup mock scenario
		ForumDto forumToAdd = new ForumDto() {{
			setTitle("title");
			setDescription("description");
			setParent(new ForumDto() {{
				setId(1);
			}});
		}};

		ForumEntity parentForumEntity = new ForumEntity() {{
			setId(1);
			setTitle("parent forum title");
		}};

		Timestamp creationTime = Timestamp.valueOf(LocalDateTime.of(2000, 1, 1, 1,1 ));

		ForumEntity createdForumEntity = new ForumEntity() {{
			setId(2);
			setTitle("title");
			setDescription("description");
			setCreateTime(creationTime);
			setParentForum(parentForumEntity);
		}};

		Mockito.doReturn(true).when(userService).isUserPermittedToModerate(Mockito.any());
		Mockito.doReturn(Optional.of(parentForumEntity)).when(forumRepository).findById(1);
		Mockito.doReturn(createdForumEntity).when(forumRepository).save(Mockito.any());
		Mockito.doReturn(createdForumEntity).when(forumRepository).getOne(2);

		// execute service method
		ForumDto returnedForum = forumService.addForum(forumToAdd);

		// perform assertions
		Assertions.assertNotNull(returnedForum);
		Assertions.assertEquals(2, returnedForum.getId());
		Assertions.assertEquals("title", returnedForum.getTitle());
		Assertions.assertEquals("description", returnedForum.getDescription());
		Assertions.assertEquals(creationTime, returnedForum.getCreateTime());
		Assertions.assertEquals(2, returnedForum.getBreadcrumb().size());
		Assertions.assertEquals(0, returnedForum.getForums().size());
		Assertions.assertEquals(0, returnedForum.getThreads().size());
		Assertions.assertTrue(returnedForum.isCanModerate());

		Assertions.assertNotNull(returnedForum.getParent());
		ForumDto parentForum = returnedForum.getParent();

		Assertions.assertEquals(1, parentForum.getId());
		Assertions.assertEquals("parent forum title", parentForum.getTitle());

	}

	@Test
	@DisplayName("Test addSubForum parent not found")
	public void testAddSubForumWithParentNotFound(){
		// setup mock scenario
		ForumDto forumToAdd = new ForumDto() {{
			setTitle("title");
			setDescription("description");
			setParent(new ForumDto() {{
				setId(1);
			}});
		}};

		Mockito.doReturn(Optional.empty()).when(forumRepository).findById(1);

		// retrieve exception from forumService method
		NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> forumService.addForum(forumToAdd));

		// perform assertions
		Assertions.assertEquals("Forum with id = 1 doesn't exist", exception.getMessage());
		Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

	}

	@Test
	@DisplayName("Test addSubForum without permission")
	public void testAddSubForumWithoutPermisison(){
		// setup mock scenario
		ForumDto forumToAdd = new ForumDto() {{
			setTitle("title");
			setDescription("description");
		}};

		Mockito.doReturn(false).when(userService).isUserPermittedToModerate(Mockito.any());

		// retrieve exception from forumService method
		UnauthorizedException exception = Assertions.assertThrows(UnauthorizedException.class, () -> forumService.addForum(forumToAdd));

		// perform assertions
		Assertions.assertEquals("You have no permission to add forum here", exception.getMessage());
		Assertions.assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
	}

	@Test
	@DisplayName("Test modifyForum success")
	void testModifyForumSuccess() throws NotFoundException, UnauthorizedException {
		// setup mock scenario
		ForumDto modifyingData = new ForumDto() {{
			setTitle("modified title");
			setDescription("modified description");
		}};

		Timestamp creationTime = Timestamp.valueOf(LocalDateTime.of(2000, 1, 1, 1,1 ));

		ForumEntity forumToModify = new ForumEntity() {{
			setId(1);
			setTitle("modified title");
			setDescription("modified description");
			setCreateTime(creationTime);
		}};



		Mockito.doReturn(true).when(userService).isUserPermittedToModerate(Mockito.any());
		Mockito.doReturn(forumToModify).when(forumRepository).getOne(1);

		// execute service method
		ForumDto returnedForum = forumService.modifyForum(modifyingData, 1);

		Assertions.assertNotNull(returnedForum);
		Assertions.assertEquals(1, returnedForum.getId());
		Assertions.assertEquals("modified title", returnedForum.getTitle());
		Assertions.assertEquals("modified description", returnedForum.getDescription());
		Assertions.assertTrue(returnedForum.isCanModerate());
		Assertions.assertEquals(1, returnedForum.getBreadcrumb().size());
		Assertions.assertEquals(0, returnedForum.getThreads().size());
		Assertions.assertEquals(0, returnedForum.getForums().size());
		Assertions.assertNull(returnedForum.getParent());

	}

	@Test
	@DisplayName("Test modifyForum not found")
	public void testModifyForumNotFound(){
		// setup mock scenario
		ForumDto modifyingData = new ForumDto() {{
			setTitle("title");
			setDescription("description");
		}};

		Mockito.doThrow(EntityNotFoundException.class).when(forumRepository).getOne(1);

		// retrieve exception from forumService method
		NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> forumService.modifyForum(modifyingData, 1));

		// perform assertions
		Assertions.assertEquals("Forum with id = 1 doesn't exist", exception.getMessage());
		Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
	}

	@Test
	@DisplayName("Test modifyForum without permission")
	public void testModfiyForumWithoutPermisison(){
		// setup mock scenario
		ForumDto modifyingData = new ForumDto() {{
			setTitle("modified title");
			setDescription("modified description");
		}};

		ForumEntity forumToModify = new ForumEntity() {{
			setId(1);
			setTitle("title");
			setDescription("description");
		}};

		Mockito.doReturn(false).when(userService).isUserPermittedToModerate(Mockito.any());
		Mockito.doReturn(forumToModify).when(forumRepository).getOne(1);

		// retrieve exception from forumService method
		UnauthorizedException exception = Assertions.assertThrows(UnauthorizedException.class, () -> forumService.modifyForum(modifyingData, 1));

		// perform assertions
		Assertions.assertEquals("You have no permission to modify this forum", exception.getMessage());
		Assertions.assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
	}

	@Test
	@DisplayName("Test deleteForum success")
	public void testDeleteForumSuccess() throws NotFoundException, UnauthorizedException {
		//setup mock scenario
		ForumEntity parentForum = new ForumEntity() {{
			setId(1);
			setTitle("parent forum title");
			setDescription("parent forum description");
		}};

		ForumEntity forumToDelete = new ForumEntity() {{
			setId(2);
			setTitle("title");
			setDescription("description");
			setParentForum(parentForum);
		}};

		Mockito.doReturn(forumToDelete).when(forumRepository).getOne(2);
		Mockito.doReturn(true).when(userService).isUserPermittedToModerate(Mockito.any());

		// execute service method
		forumService.deleteForum(2);

		// no response expected

	}

	@Test
	@DisplayName("Test deleteForum not found")
	public void testDeleteForumNotfound() {
		// set up mock scenario
		Mockito.doThrow(EntityNotFoundException.class).when(forumRepository).getOne(1);

		// execute service method
		NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> forumService.deleteForum(1));

		// perform assertions
		Assertions.assertEquals("Forum with id = 1 doesn't exist", exception.getMessage());
		Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
	}

	@Test
	@DisplayName("Test deleteForum forbidden")
	public void testDeleteForumForbidden() {
		// set up mock scenario
		ForumEntity forumToDelete = new ForumEntity() {{
			setId(1);
			setTitle("title");
			setDescription("description");
		}};

		Mockito.doReturn(forumToDelete).when(forumRepository).getOne(1);
		Mockito.doReturn(false).when(userService).isUserPermittedToModerate(Mockito.any());

		// execute service method
		UnauthorizedException exception = Assertions.assertThrows(UnauthorizedException.class, () -> forumService.deleteForum(1));

		// perform assertions
		Assertions.assertEquals("You have no permission to delete this forum", exception.getMessage());
		Assertions.assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
	}

	@Test
	@DisplayName("Test getBreadcrumb")
	public void testGetBreadcrumb() {
		// set up mock scenario
		ForumEntity parentForum1 = new ForumEntity() {{
			setId(1);
			setTitle("title 1");
		}};

		ForumEntity parentForum2 = new ForumEntity() {{
			setId(2);
			setTitle("title 2");
			setParentForum(parentForum1);
		}};

		ForumEntity forumEntity = new ForumEntity() {{
			setId(3);
			setTitle("title 3");
			setParentForum(parentForum2);
		}};

		// execute service method
		List<ForumDto> breadcrumb = forumService.getBreadcrumb(forumEntity);

		// perform assertions
		Assertions.assertEquals(3, breadcrumb.size());
		Assertions.assertEquals(1, breadcrumb.get(0).getId());
		Assertions.assertEquals("title 1", breadcrumb.get(0).getTitle());
		Assertions.assertEquals(2, breadcrumb.get(1).getId());
		Assertions.assertEquals("title 2", breadcrumb.get(1).getTitle());
		Assertions.assertEquals(3, breadcrumb.get(2).getId());
		Assertions.assertEquals("title 3", breadcrumb.get(2).getTitle());

	}

}











