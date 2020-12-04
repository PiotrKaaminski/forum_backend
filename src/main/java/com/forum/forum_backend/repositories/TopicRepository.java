package com.forum.forum_backend.repositories;

import com.forum.forum_backend.models.TopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<TopicEntity, Integer> {
}
