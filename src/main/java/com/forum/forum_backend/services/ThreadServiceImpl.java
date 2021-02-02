package com.forum.forum_backend.services;

import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.dtos.PostDto;
import com.forum.forum_backend.dtos.ThreadDto;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.ThreadEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.repositories.ThreadRepository;
import com.forum.forum_backend.repositories.UserRepository;
import com.forum.forum_backend.services.interfaces.ThreadService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Transactional
@Service
public class ThreadServiceImpl implements ThreadService {
	
	private final ThreadRepository threadRepository;
	private final UserRepository userRepository;
	private final UserService userService;

	public ThreadServiceImpl(ThreadRepository threadRepository, UserRepository userRepository, UserService userService) {
		this.threadRepository = threadRepository;
		this.userRepository = userRepository;
		this.userService = userService;
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
			thread.setThreadAuthor(threadAuthor);
			thread.setLikesAmount(threadEntity.getUsersLikes().size());
			thread.setPostsAmount(null);

			thread.setPosts(new ArrayList<>() {{
				addAll(threadEntity.getPosts().stream().map(x -> new PostDto() {{
					setId(x.getId());
					setMessage(x.getMessage());

					UserDto postAuthor = new UserDto();
					postAuthor.setId(x.getUser().getId());
					postAuthor.setUsername(x.getUser().getUsername());

					setPostAuthor(postAuthor);
					setLikesAmount(x.getUsersLikes().size());
				}}).collect(Collectors.toList()));
			}});

			return thread;

		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Thread with id = " + threadId + " doesn't exist");
		}
	}

	@Override
	public void addThread(ThreadDto threadDto) {
		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = user.getId();
		UserEntity owner = userService.getUserById(userId);

		ThreadEntity thread = new ThreadEntity(threadDto.getTitle(), threadDto.getMessage(), owner);
		threadRepository.save(thread);
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

			if (userService.isUserAnAuthor(thread.getUser())) {
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

			if (userService.isUserAnAuthor(thread.getUser())) {
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

			UserDto author = new UserDto();
			author.setId(threadEntity.getUser().getId());
			author.setUsername(threadEntity.getUser().getUsername());
			setThreadAuthor(author);

			setPostsAmount(threadEntity.getPosts().size());
			setLikesAmount(threadEntity.getUsersLikes().size());
		}};
	}
}
