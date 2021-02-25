package com.forum.forum_backend.repositories;

import com.forum.forum_backend.models.AuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Integer> {

	AuthorityEntity findByName(String name);

}
