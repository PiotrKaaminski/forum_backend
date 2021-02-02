package com.forum.forum_backend.repositories;

import com.forum.forum_backend.models.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Integer> {
}
