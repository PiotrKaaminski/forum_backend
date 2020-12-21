package com.forum.forum_backend.controllers;

import com.forum.forum_backend.dtos.CategoryDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.services.interfaces.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void addMainCategory(@RequestBody CategoryDto categoryDto) {
		categoryService.addMainCategory(categoryDto);
	}

	@PostMapping("/{categoryId}")
	@ResponseStatus(HttpStatus.CREATED)
	public void addSubCategory(@RequestBody CategoryDto categoryDto, @PathVariable int categoryId)
			throws UnauthorizedException, NotFoundException {
		categoryService.addSubCategory(categoryDto, categoryId);
	}

}
