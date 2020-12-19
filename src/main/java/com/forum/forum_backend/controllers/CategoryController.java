package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.CategoryDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.services.interfaces.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	private CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@GetMapping
	public List<CategoryDto> getMainCategoryList() {
		return categoryService.getMainCategoryList();
	}

	@GetMapping("/{categoryId}")
	public CategoryDto getSubCategory(@PathVariable int categoryId) throws NotFoundException {
		return categoryService.getSubCategory(categoryId);
	}

}
