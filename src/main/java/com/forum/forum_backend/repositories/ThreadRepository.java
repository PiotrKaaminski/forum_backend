package com.forum.forum_backend.repositories;

import com.forum.forum_backend.models.ThreadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadRepository extends JpaRepository<ThreadEntity, Integer> {
}
