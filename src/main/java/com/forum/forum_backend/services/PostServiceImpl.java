package com.forum.forum_backend.services;

import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.dtos.PostDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.PostEntity;
import com.forum.forum_backend.models.ThreadEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.repositories.PostRepository;
import com.forum.forum_backend.repositories.ThreadRepository;
import com.forum.forum_backend.repositories.UserRepository;
import com.forum.forum_backend.services.interfaces.PostService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Transactional
@Service
public class PostServiceImpl implements PostService {

	private final PostRepository postRepository;
	private final ThreadRepository threadRepository;
	private final UserService userService;
	private final UserRepository userRepository;

	public PostServiceImpl(PostRepository postRepository, ThreadRepository threadRepository, UserService userService, UserRepository userRepository) {
		this.postRepository = postRepository;
		this.threadRepository = threadRepository;
		this.userService = userService;
		this.userRepository = userRepository;
	}

	@Override
	public void addPost( int threadId, PostDto postDto) throws NotFoundException {
		try {
			ThreadEntity thread = threadRepository.getOne(threadId);

			PostEntity post = new PostEntity();

			UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			int userId = user.getId();
			UserEntity owner = userService.getUserById(userId);

			post.setMessage(postDto.getMessage());
			post.setUser(owner);
			post.setThread(thread);

			postRepository.save(post);
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Thread with id = " + threadId + " doesn't exist");
		}
	}

	@Override
	public void addLike(int postId) throws NotFoundException {
		try {
			PostEntity post = postRepository.getOne(postId);

			UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			UserEntity user = userService.getUserById(userPrincipal.getId());

			if(user.getLikedPosts().contains(post)){
				user.getLikedPosts().remove(post);
			} else {
				user.addPostLike(post);
				userRepository.save(user);
			}

		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Post with id = " + postId + " doesn't exist");
		}
	}

	@Override
	public void modifyPost(int postId, PostDto postDto) throws UnauthorizedException, NotFoundException {
		try {
			PostEntity post = postRepository.getOne(postId);

			if (userService.isUserAnAuthor(post.getUser())) {
				post.setMessage(postDto.getMessage());
			} else {
				throw new UnauthorizedException("You have no permissions to modify this post");
			}
			postRepository.save(post);
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Post with id = " + postId + " doesn't exist");
		}

	}

	@Override
	public void deletePost(int postId) throws UnauthorizedException, NotFoundException {
		try {
			PostEntity post = postRepository.getOne(postId);

			if (userService.isUserAnAuthor(post.getUser())) {
				postRepository.delete(post);
			} else {
				throw new UnauthorizedException("You have no permissions to delete this post");
			}
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Post with id = " + postId + " doesn't exist");
		}
	}

}
