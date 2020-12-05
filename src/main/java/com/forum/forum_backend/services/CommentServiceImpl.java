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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private TopicRepository topicRepository;
	@Autowired
	private UserService userService;

	@Override
	public void addComment(CommentDto commentDto, int topicId) {
		TopicEntity topic = topicRepository.getOne(topicId);

		CommentEntity comment = new CommentEntity();

		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = user.getId();
		UserEntity owner = userService.getUserById(userId);

		comment.setContent(commentDto.getContent());
		comment.setUser(owner);
		comment.setTopic(topic);

		topic.addComment(comment);

		topicRepository.save(topic);
	}
}
