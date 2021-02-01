package com.forum.forum_backend.repositories;

import com.forum.forum_backend.models.ThreadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<ThreadEntity, Integer> {
}
