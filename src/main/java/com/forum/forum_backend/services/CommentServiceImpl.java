package com.forum.forum_backend.services;

import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.dtos.CommentDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.CommentEntity;
import com.forum.forum_backend.models.TopicEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.repositories.CommentRepository;
import com.forum.forum_backend.repositories.TopicRepository;
import com.forum.forum_backend.repositories.UserRepository;
import com.forum.forum_backend.services.interfaces.CommentService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Transactional
@Service
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	private final TopicRepository topicRepository;
	private final UserService userService;
	private final UserRepository userRepository;

	public CommentServiceImpl(CommentRepository commentRepository, TopicRepository topicRepository, UserService userService, UserRepository userRepository) {
		this.commentRepository = commentRepository;
		this.topicRepository = topicRepository;
		this.userService = userService;
		this.userRepository = userRepository;
	}

	@Override
	public void addComment( int topicId, CommentDto commentDto) throws NotFoundException {
		try {
			TopicEntity topic = topicRepository.getOne(topicId);

			CommentEntity comment = new CommentEntity();

			UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			int userId = user.getId();
			UserEntity owner = userService.getUserById(userId);

			comment.setContent(commentDto.getContent());
			comment.setUser(owner);
			comment.setTopic(topic);

			commentRepository.save(comment);
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Topic with id = " + topicId + " doesn't exist");
		}
	}

	@Override
	public void addLike(int commentId) throws NotFoundException {
		try {
			CommentEntity comment = commentRepository.getOne(commentId);

			UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			UserEntity user = userService.getUserById(userPrincipal.getId());

			if(user.getLikedComments().contains(comment)){
				user.getLikedComments().remove(comment);
			} else {
				user.addCommentLike(comment);
				userRepository.save(user);
			}

		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Comment with id = " + commentId + " doesn't exist");
		}
	}

	@Override
	public void modifyComment(int commentId, CommentDto commentDto) throws UnauthorizedException, NotFoundException {
		try {
			CommentEntity comment = commentRepository.getOne(commentId);

			if (userService.isUserAuthorized(comment.getUser())) {
				comment.setContent(commentDto.getContent());
			} else {
				throw new UnauthorizedException("You have no permissions to modify this comment");
			}
			commentRepository.save(comment);
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Comment with id = " + commentId + " doesn't exist");
		}

	}

	@Override
	public void deleteComment(int commentId) throws UnauthorizedException, NotFoundException {
		try {
			CommentEntity comment = commentRepository.getOne(commentId);

			if (userService.isUserAuthorized(comment.getUser())) {
				commentRepository.delete(comment);
			} else {
				throw new UnauthorizedException("You have no permissions to delete this comment");
			}
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Comment with id = " + commentId + " doesn't exist");
		}
	}

}
