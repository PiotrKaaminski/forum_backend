package com.forum.forum_backend.repositories;

import com.forum.forum_backend.models.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

	UserEntity findByUsername(String username);
	boolean existsByUsername(String username);
	Page<UserEntity> findByUsernameStartsWith(String username, Pageable pageable);

}
