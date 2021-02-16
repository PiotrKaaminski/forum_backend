package com.forum.forum_backend.repositories;

import com.forum.forum_backend.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

	UserEntity findByUsername(String username);
	boolean existsByUsername(String username);
	List<UserEntity> findByUsernameStartsWith(String username);

}
