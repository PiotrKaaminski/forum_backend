package com.forum.forum_backend.services;
import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.dtos.CommentDto;
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
	public void modifyComment(int commentId, CommentDto commentDto) {
		CommentEntity comment = commentRepository.getOne(commentId);

		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = user.getId();

		if (comment.getUser().getId() == userId) {
			comment.setContent(commentDto.getContent());
		}

		commentRepository.save(comment);

	}

	@Override
	public void deleteComment(int commentId) {
		CommentEntity comment = commentRepository.getOne(commentId);

		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = user.getId();

		if (comment.getUser().getId() == userId) {
			commentRepository.delete(comment);
		}
	}
}
