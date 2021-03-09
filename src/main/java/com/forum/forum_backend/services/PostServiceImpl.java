package com.forum.forum_backend.services;

import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.dtos.PaginatedResponse;
import com.forum.forum_backend.dtos.PostDto;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.exceptions.BadRequestException;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.PostEntity;
import com.forum.forum_backend.models.ThreadEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.repositories.PostRepository;
import com.forum.forum_backend.repositories.ThreadRepository;
import com.forum.forum_backend.services.interfaces.PostService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Transactional
@Service
public class PostServiceImpl implements PostService {

	private final PostRepository postRepository;
	private final ThreadRepository threadRepository;
	private final UserService userService;

	public PostServiceImpl(PostRepository postRepository, ThreadRepository threadRepository, UserService userService) {
		this.postRepository = postRepository;
		this.threadRepository = threadRepository;
		this.userService = userService;
	}

	@Override
	public PostDto addPost(int threadId, PostDto postDto) throws NotFoundException, BadRequestException {
		try {
			ThreadEntity thread = threadRepository.getOne(threadId);

			if (thread.isLocked()) {
				throw new BadRequestException("Thread with id = " + threadId + " is locked, cannot add post");
			}

			PostEntity post = new PostEntity();

			UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			int userId = user.getId();
			UserEntity owner = userService.getUserById(userId);

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());

			post.setMessage(postDto.getMessage());
			post.setUser(owner);
			post.setThread(thread);
			post.setCreateTime(timestamp);

			int postId = postRepository.save(post).getId();

			return getPost(postId);
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Thread with id = " + threadId + " doesn't exist");
		}
	}

	@Override
	public PostDto modifyPost(int postId, PostDto postDto) throws UnauthorizedException, NotFoundException {
		try {
			PostEntity post = postRepository.getOne(postId);

			if (postDto.getMessage() != null) {
				if (!userService.isUserAnAuthor(post.getUser()) && !userService.isUserPermittedToModerate(post.getThread().getParentForum())) {
					throw new UnauthorizedException("You have no permissions to modify this post");
				}
				post.setMessage(postDto.getMessage());
			}

			if (postDto.isLiked() != null) {
				post.setUsersLikes(userService.setLikes(post.getUsersLikes(), postDto.isLiked()));
			}

			postRepository.save(post);

			return getPost(postId);
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Post with id = " + postId + " doesn't exist");
		}

	}

	@Override
	public void deletePost(int postId) throws UnauthorizedException, NotFoundException {
		try {
			PostEntity post = postRepository.getOne(postId);

			if (userService.isUserAnAuthor(post.getUser()) || userService.isUserPermittedToModerate(post.getThread().getParentForum())) {
				postRepository.delete(post);
			} else {
				throw new UnauthorizedException("You have no permissions to delete this post");
			}
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Post with id = " + postId + " doesn't exist");
		}
	}

	public PostDto getPost(int postId) throws NotFoundException {
		try {
			PostEntity postEntity = postRepository.getOne(postId);
			PostDto post = new PostDto();
			post.setId(postEntity.getId());
			post.setMessage(postEntity.getMessage());

			UserDto postAuthor = new UserDto();
			postAuthor.setId(postEntity.getUser().getId());
			postAuthor.setUsername(postEntity.getUser().getUsername());
			post.setPostAuthor(postAuthor);

			post.setThreadId(postEntity.getThread().getId());
			post.setLikesAmount(postEntity.getUsersLikes().size());
			post.setCreateTime(postEntity.getCreateTime());

			post.setCanModerate(userService.isUserAnAuthor(postEntity.getUser()) || userService.isUserPermittedToModerate(postEntity.getThread().getParentForum()));

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserEntity userEntity = null;
			if (!authentication.getPrincipal().equals("anonymousUser")) {
				userEntity = userService.getUserById(((UserPrincipal) authentication.getPrincipal()).getId());
			}
			post.setLiked(postEntity.getUsersLikes().contains(userEntity));

			return post;
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Post with id = " + postId + " doesn't exist");
		}
	}

	@Override
	public PaginatedResponse<PostDto> getPostsByThread(int threadId, int size, int page) {
		Pageable pageable = PageRequest.of(page, size);
		Page<PostEntity> postEntityPage = postRepository.findByThreadId(threadId, pageable);

		PaginatedResponse<PostDto> response = new PaginatedResponse<>();

		response.setResults(new ArrayList<>() {{
			addAll(postEntityPage.stream().map(x -> new PostDto() {{
				setId(x.getId());
				setMessage(x.getMessage());

				UserDto postAuthor = new UserDto();
				postAuthor.setId(x.getUser().getId());
				postAuthor.setUsername(x.getUser().getUsername());

				setCanModerate(userService.isUserAnAuthor(x.getUser()) || userService.isUserPermittedToModerate(x.getThread().getParentForum()));

				setThreadId(x.getThread().getId());
				setPostAuthor(postAuthor);
				setLikesAmount(x.getUsersLikes().size());
				setCreateTime(x.getCreateTime());

				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				UserEntity userEntity = null;
				if (!authentication.getPrincipal().equals("anonymousUser")) {
					userEntity = userService.getUserById(((UserPrincipal) authentication.getPrincipal()).getId());
				}
				setLiked(x.getUsersLikes().contains(userEntity));
			}}).collect(Collectors.toList()));
		}});
		response.setCount((int) postEntityPage.getTotalElements());

		return response;
	}
}
