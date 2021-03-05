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
import com.forum.forum_backend.repositories.UserRepository;
import com.forum.forum_backend.services.interfaces.ForumService;
import com.forum.forum_backend.services.interfaces.PostService;
import com.forum.forum_backend.services.interfaces.ThreadService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.context.annotation.Lazy;
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
	private final UserRepository userRepository;
	private final UserService userService;
	private final ForumService forumService;
	private final PostService postService;

	public ThreadServiceImpl(
			ForumRepository forumRepository,
			ThreadRepository threadRepository,
			UserRepository userRepository,
			UserService userService,
			@Lazy ForumService forumService,
			PostService postService) {
		this.forumRepository = forumRepository;
		this.threadRepository = threadRepository;
		this.userRepository = userRepository;
		this.userService = userService;
		this.forumService = forumService;
		this.postService = postService;
	}

	@Override
	public ThreadDto getThread(int threadId, int size, int page) throws NotFoundException {
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


			thread.setCanModerate(userService.isUserAnAuthor(threadEntity.getUser()) || userService.isUserPermittedToModerate(threadEntity.getParentForum()));

			thread.setPosts(postService.getPostsByThread(threadEntity.getId(), size, page));

			return thread;

		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Thread with id = " + threadId + " doesn't exist");
		}
	}

	@Override
	public ThreadDto addThread(ThreadDto threadDto) throws NotFoundException {
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
			return getThread(threadId, 1, 0);
		} catch (EntityNotFoundException | UnauthorizedException ex) {
			throw new NotFoundException("Forum with id = " + forumId + " doesn't exist");
		}
	}

	@Override
	public void addLike(int threadId) throws NotFoundException {
		try {
			ThreadEntity thread = threadRepository.getOne(threadId);

			UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			UserEntity user = userService.getUserById(userPrincipal.getId());

			if(user.getLikedThreads().contains(thread)){
				user.getLikedThreads().remove(thread);
			} else {
				user.addThreadLike(thread);
				userRepository.save(user);
			}

		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Thread with id = " + threadId + " doesn't exist");
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
		}};
	}

	@Override
	public void modifyThread(int threadId, ThreadDto threadDto) throws NotFoundException, UnauthorizedException {
		try {
			ThreadEntity thread = threadRepository.getOne(threadId);

			if (userService.isUserAnAuthor(thread.getUser()) || userService.isUserPermittedToModerate(thread.getParentForum())) {
				thread.setLocked(threadDto.isLocked());
				threadRepository.save(thread);
			} else {
				throw new UnauthorizedException("You have no permissions to modify this thread");
			}
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Thread with id = " + threadId + " doesn't exist");
		}
	}
}
