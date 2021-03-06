package com.forum.forum_backend.services;

import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.dtos.ThreadDto;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.ForumEntity;
import com.forum.forum_backend.models.ThreadEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.repositories.ForumRepository;
import com.forum.forum_backend.repositories.ThreadRepository;
import com.forum.forum_backend.services.interfaces.ForumService;
import com.forum.forum_backend.services.interfaces.PostService;
import com.forum.forum_backend.services.interfaces.ThreadService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;

@Transactional
@Service
public class ThreadServiceImpl implements ThreadService {

	private final ForumRepository forumRepository;
	private final ThreadRepository threadRepository;
	private final UserService userService;
	private final ForumService forumService;
	private final PostService postService;

	public ThreadServiceImpl(
			ForumRepository forumRepository,
			ThreadRepository threadRepository,
			UserService userService,
			@Lazy ForumService forumService,
			PostService postService) {
		this.forumRepository = forumRepository;
		this.threadRepository = threadRepository;
		this.userService = userService;
		this.forumService = forumService;
		this.postService = postService;
	}

	private ThreadDto getThread(int threadId, Integer size, Integer page, boolean getPosts) throws NotFoundException {
		try {
			ThreadEntity threadEntity = threadRepository.getOne(threadId);
			ThreadDto thread = new ThreadDto();
			thread.setId(threadEntity.getId());
			thread.setTitle(threadEntity.getTitle());
			thread.setMessage(threadEntity.getMessage());

			UserDto threadAuthor = new UserDto();
			threadAuthor.setId(threadEntity.getUser().getId());
			threadAuthor.setUsername(threadEntity.getUser().getUsername());

			thread.setLocked(threadEntity.isLocked());
			thread.setCreator(threadAuthor);
			thread.setLikesAmount(threadEntity.getUsersLikes().size());
			thread.setCreateTime(threadEntity.getCreateTime());
			thread.setBreadcrumb(forumService.getBreadcrumb(threadEntity.getParentForum()));

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserEntity userEntity = null;
			if (!authentication.getPrincipal().equals("anonymousUser")) {
				userEntity = userService.getUserById(((UserPrincipal) authentication.getPrincipal()).getId());
			}
			thread.setLiked(threadEntity.getUsersLikes().contains(userEntity));

			thread.setCanModerate(userService.isUserAnAuthor(threadEntity.getUser()) || userService.isUserPermittedToModerate(threadEntity.getParentForum()));

			if(getPosts) {
				thread.setPosts(postService.getPostsByThread(threadEntity.getId(), size, page));
			}

			return thread;

		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Thread with id = " + threadId + " doesn't exist");
		}
	}

	@Override
	public ThreadDto getThread(int threadId, int size, int page) throws NotFoundException {
		return getThread(threadId, size, page, true);
	}

	private ThreadDto getThread(int threadId) throws NotFoundException {
		return getThread(threadId, null, null, false);
	}


	@Override
	public ThreadDto addThread(ThreadDto threadDto) throws NotFoundException, UnauthorizedException {
		int forumId = threadDto.getForum().getId();
		try {
			ForumEntity parentForum = forumRepository.getOne(forumId);

			if (parentForum.getParentForum() == null) {
				throw new UnauthorizedException("Cannot add thread to main forum");
			}

			UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			int userId = user.getId();
			UserEntity owner = userService.getUserById(userId);

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());

			ThreadEntity thread = new ThreadEntity(threadDto.getTitle(), threadDto.getMessage(), owner, timestamp);
			thread.setParentForum(parentForum);
			int threadId = threadRepository.save(thread).getId();
			return getThread(threadId);
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Forum with id = " + forumId + " doesn't exist");
		}
	}

	@Override
	public void deleteThread(int threadId) throws UnauthorizedException, NotFoundException {
		try {
			ThreadEntity thread = threadRepository.getOne(threadId);

			if (userService.isUserAnAuthor(thread.getUser()) || userService.isUserPermittedToModerate(thread.getParentForum())) {
				threadRepository.delete(thread);
			} else {
				throw new UnauthorizedException("You have no permissions to delete this thread");
			}
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Thread with id = " + threadId + " doesn't exist");
		}
	}



	@Override
	public ThreadDto mapChildEntityToDto(ThreadEntity threadEntity) {
		return new ThreadDto() {{
			setId(threadEntity.getId());
			setTitle(threadEntity.getTitle());
			setCreateTime(threadEntity.getCreateTime());

			setCanModerate(userService.isUserPermittedToModerate(threadEntity.getParentForum()) || userService.isUserAnAuthor(threadEntity.getUser()));
			setLocked(threadEntity.isLocked());

			UserDto author = new UserDto();
			author.setId(threadEntity.getUser().getId());
			author.setUsername(threadEntity.getUser().getUsername());
			setCreator(author);

			setPostsAmount(threadEntity.getPosts().size());
			setLikesAmount(threadEntity.getUsersLikes().size());

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserEntity userEntity = null;
			if (!authentication.getPrincipal().equals("anonymousUser")) {
				userEntity = userService.getUserById(((UserPrincipal) authentication.getPrincipal()).getId());
			}

			setLiked(threadEntity.getUsersLikes().contains(userEntity));
		}};
	}

	@Override
	public ThreadDto modifyThread(int threadId, ThreadDto threadDto) throws NotFoundException, UnauthorizedException {
		try {
			ThreadEntity thread = threadRepository.getOne(threadId);

			if (threadDto.isLocked() != null) {
				if (!userService.isUserAnAuthor(thread.getUser()) && !userService.isUserPermittedToModerate(thread.getParentForum())) {
					throw new UnauthorizedException("You have no permissions to modify this thread");
				}
				thread.setLocked(threadDto.isLocked());
			}

			if (threadDto.isLiked() != null) {
				thread.setUsersLikes(userService.setLikes(thread.getUsersLikes(), threadDto.isLiked()));
			}

			threadRepository.save(thread);
			return getThread(threadId);

		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Thread with id = " + threadId + " doesn't exist");
		}
	}
}
