package com.forum.forum_backend.services;
import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.dtos.CommentDto;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.CommentEntity;
import com.forum.forum_backend.models.TopicEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.repositories.CommentRepository;
import com.forum.forum_backend.repositories.TopicRepository;
import com.forum.forum_backend.services.interfaces.CommentService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	private final TopicRepository topicRepository;
	private final UserService userService;

	public CommentServiceImpl(CommentRepository commentRepository, TopicRepository topicRepository, UserService userService) {
		this.commentRepository = commentRepository;
		this.topicRepository = topicRepository;
		this.userService = userService;
	}

	@Override
	public void addComment( int topicId, CommentDto commentDto) {
		TopicEntity topic = topicRepository.getOne(topicId);

		CommentEntity comment = new CommentEntity();

		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = user.getId();
		UserEntity owner = userService.getUserById(userId);

		comment.setContent(commentDto.getContent());
		comment.setUser(owner);
		comment.setTopic(topic);

		commentRepository.save(comment);
	}

	@Override
	public void modifyComment(int commentId, CommentDto commentDto) throws UnauthorizedException {
		CommentEntity comment = commentRepository.getOne(commentId);

		if (isUserTheAuthor(comment)) {
			comment.setContent(commentDto.getContent());
		} else {
			throw new UnauthorizedException("You have no permissions to modify this comment");
		}

		commentRepository.save(comment);

	}

	@Override
	public void deleteComment(int commentId) throws UnauthorizedException {
		CommentEntity comment = commentRepository.getOne(commentId);

		if (isUserTheAuthor(comment)) {
			commentRepository.delete(comment);
		} else {
			throw new UnauthorizedException("You have no permissions to delete this comment");
		}
	}

	private boolean isUserTheAuthor(CommentEntity comment) {
		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = user.getId();

		return comment.getUser().getId() == userId;
	}
}
