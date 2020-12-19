package com.forum.forum_backend.repositories;

import com.forum.forum_backend.models.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {

	List<CategoryEntity> findAllByParentCategoryId(Integer parentId);

}
