package com.forum.forum_backend.services;

import com.forum.forum_backend.repositories.CommentRepository;
import com.forum.forum_backend.services.interfaces.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentRepository commentRepository;

}
