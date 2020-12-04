package com.forum.forum_backend.repositories;

import com.forum.forum_backend.models.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
}
