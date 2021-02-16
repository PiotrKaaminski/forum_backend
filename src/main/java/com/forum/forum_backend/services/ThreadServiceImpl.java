package com.forum.forum_backend.services;

import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.dtos.*;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.ForumEntity;
import com.forum.forum_backend.models.ThreadEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.repositories.ForumRepository;
import com.forum.forum_backend.repositories.ThreadRepository;
import com.forum.forum_backend.repositories.UserRepository;
import com.forum.forum_backend.services.interfaces.ForumService;
import com.forum.forum_backend.services.interfaces.ThreadService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Transactional
@Service
public class ThreadServiceImpl implements ThreadService {

	private final ForumRepository forumRepository;
	private final ThreadRepository threadRepository;
	private final UserRepository userRepository;
	private final UserService userService;
	private final ForumService forumService;

	public ThreadServiceImpl(
			ForumRepository forumRepository,
			ThreadRepository threadRepository,
			UserRepository userRepository,
			UserService userService,
			@Lazy ForumService forumService) {
		this.forumRepository = forumRepository;
		this.threadRepository = threadRepository;
		this.userRepository = userRepository;
		this.userService = userService;
		this.forumService = forumService;
	}

	@Override
	public ThreadDto getThread(int threadId) throws NotFoundException {
		try {
			ThreadEntity threadEntity = threadRepository.getOne(threadId);
			ThreadDto thread = new ThreadDto();
			thread.setId(threadEntity.getId());
			thread.setTitle(threadEntity.getTitle());
			thread.setMessage(threadEntity.getMessage());

			UserDto threadAuthor = new UserDto();
			threadAuthor.setId(threadEntity.getUser().getId());
			threadAuthor.setUsername(threadEntity.getUser().getUsername());
			thread.setCreator(threadAuthor);
			thread.setLikesAmount(threadEntity.getUsersLikes().size());
			thread.setPostsAmount(null);
			thread.setCreateTime(threadEntity.getCreateTime());
			thread.setBreadcrumb(forumService.getBreadcrumb(threadEntity.getParentForum()));


			thread.setCanModerate(userService.isUserAnAuthor(threadEntity.getUser()) || userService.isUserPermittedToModerate(threadEntity.getParentForum()));

			thread.setPosts(new PaginatedResponse<>() {{
				setResults(new ArrayList<>() {{
					addAll(threadEntity.getPosts().stream().map(x -> new PostDto() {{
						setId(x.getId());
						setMessage(x.getMessage());

						UserDto postAuthor = new UserDto();
						postAuthor.setId(x.getUser().getId());
						postAuthor.setUsername(x.getUser().getUsername());

						setCanModerate(userService.isUserAnAuthor(x.getUser()) || userService.isUserPermittedToModerate(x.getThread().getParentForum()));

						setPostAuthor(postAuthor);
						setLikesAmount(x.getUsersLikes().size());
						setCreateTime(x.getCreateTime());
					}}).collect(Collectors.toList()));
				}});
				setCount(getResults().size());
			}});

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
			return getThread(threadId);
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
	public void modifyThread(ThreadDto threadDto, int threadId) throws UnauthorizedException, NotFoundException {

		try {
			ThreadEntity thread = threadRepository.getOne(threadId);

			if (userService.isUserAnAuthor(thread.getUser()) || userService.isUserPermittedToModerate(thread.getParentForum())) {
				if (threadDto.getTitle() != null) {
					thread.setTitle(threadDto.getTitle());
				}
				if (threadDto.getMessage() != null) {
					thread.setMessage(threadDto.getMessage());
				}
				threadRepository.save(thread);
			} else {
				throw new UnauthorizedException("You have no permissions to modify this thread");
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
	public void toggleLocked(int threadId, LockThreadDto lockThreadDto) throws NotFoundException, UnauthorizedException {
		try {
			ThreadEntity threadEntity = threadRepository.getOne(threadId);

			if (userService.isUserAnAuthor(threadEntity.getUser()) || userService.isUserPermittedToModerate(threadEntity.getParentForum())) {
				threadEntity.setLocked(lockThreadDto.isLocked());
			} else {
				throw new UnauthorizedException("You have no permission to lock thread with id = " + threadId);
			}

		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Thread with id = " + threadId + " doesn't exist");
		}
	}
}
