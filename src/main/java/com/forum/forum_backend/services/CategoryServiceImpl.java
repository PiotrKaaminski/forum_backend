package com.forum.forum_backend.services;

import com.forum.forum_backend.dtos.CategoryDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.CategoryEntity;
import com.forum.forum_backend.repositories.CategoryRepository;
import com.forum.forum_backend.services.interfaces.CategoryService;
import com.forum.forum_backend.services.interfaces.TopicService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;
	private final TopicService topicService;
	private final UserService userService;

	public CategoryServiceImpl(CategoryRepository categoryRepository, TopicService topicService, UserService userService) {
		this.categoryRepository = categoryRepository;
		this.topicService = topicService;
		this.userService = userService;
	}

	@Override
	public List<CategoryDto> getMainCategoryList() {
		List<CategoryEntity> categoryEntities = categoryRepository.findAllByParentCategoryId(null);

		return new ArrayList<>() {{
			addAll(categoryEntities.stream().map(x -> new CategoryDto() {{
				setId(x.getId());
				setTitle(x.getTitle());
				if (!x.getChildCategories().isEmpty()) {
					setChildCategories(
							x.getChildCategories()
									.stream().map(CategoryServiceImpl.this::mapChildEntityToDto)
									.collect(Collectors.toList())
					);
				} else if (!x.getTopicEntities().isEmpty()) {
					setTopics(
							x.getTopicEntities()
									.stream().map(topicService::mapChildEntityToDto)
									.collect(Collectors.toList())
					);
				}
			}}).collect(Collectors.toList()));
		}};
	}

	@Override
	public CategoryDto getSubCategory(int categoryId) throws NotFoundException {
		try {
			CategoryEntity categoryEntity = categoryRepository.getOne(categoryId);

			CategoryDto category = new CategoryDto();
			category.setId(categoryEntity.getId());
			category.setTitle(categoryEntity.getTitle());

			if (categoryEntity.getParentCategory() != null) {
				category.setParentId(categoryEntity.getParentCategory().getId());
			}

			if (!categoryEntity.getChildCategories().isEmpty()) {
				category.setChildCategories(
						categoryEntity.getChildCategories()
								.stream().map(this::mapChildEntityToDto)
								.collect(Collectors.toList())
				);

			} else if (!categoryEntity.getTopicEntities().isEmpty()) {
				category.setTopics(
						categoryEntity.getTopicEntities()
								.stream().map(topicService::mapChildEntityToDto)
								.collect(Collectors.toList())
				);
			}

			return category;
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Category with id = " + categoryId + " doesn't exist");
		}
	}

	@Override
	public void addMainCategory(CategoryDto categoryDto){
		CategoryEntity category = new CategoryEntity();
		category.setTitle(categoryDto.getTitle());
		categoryRepository.save(category);
	}

	@Override
	public void addSubCategory(CategoryDto categoryDto, int parentCategoryId)
			throws UnauthorizedException, NotFoundException {
		try {
			CategoryEntity parentCategoryEntity = categoryRepository.getOne(parentCategoryId);
			CategoryEntity categoryEntity = new CategoryEntity();
			categoryEntity.setTitle(categoryDto.getTitle());
			categoryEntity.setParentCategory(parentCategoryEntity);

			if (parentCategoryEntity.getTopicEntities().isEmpty()) {
				if (userService.isUserPermittedToModerate(parentCategoryEntity)) {
					categoryRepository.save(categoryEntity);
				} else {
					throw new UnauthorizedException("You have no permission to add category");
				}
			} else {
				throw new UnauthorizedException("Category already contains topics");
			}

		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Category with id: " + parentCategoryId + " does'n exist");
		}
	}

	private CategoryDto mapChildEntityToDto(CategoryEntity categoryEntity) {
		return new CategoryDto() {{
			setId(categoryEntity.getId());
			setTitle(categoryEntity.getTitle());

			if (!categoryEntity.getChildCategories().isEmpty()) {
				setChildrenAmount(categoryEntity.getChildCategories().size());
			} else if (!categoryEntity.getTopicEntities().isEmpty()) {
				setChildrenAmount(categoryEntity.getTopicEntities().size());
			} else {
				setChildrenAmount(0);
			}
		}};
	}
}
