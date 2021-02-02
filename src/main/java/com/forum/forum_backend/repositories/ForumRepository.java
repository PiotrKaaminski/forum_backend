package com.forum.forum_backend.repositories;

import com.forum.forum_backend.models.ForumEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumRepository extends JpaRepository<ForumEntity, Integer> {

	List<ForumEntity> findAllByParentForumId(Integer parentId);

}
