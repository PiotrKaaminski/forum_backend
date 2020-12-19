package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.CategoryDto;
import com.forum.forum_backend.exceptions.NotFoundException;

import java.util.List;

public interface CategoryService {
	List<CategoryDto> getMainCategoryList();
	CategoryDto getSubCategory(int categoryId) throws NotFoundException;
}
