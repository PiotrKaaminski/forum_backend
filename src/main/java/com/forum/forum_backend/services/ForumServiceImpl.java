package com.forum.forum_backend.services;

import com.forum.forum_backend.dtos.ForumDto;
import com.forum.forum_backend.dtos.PaginatedResponse;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.ForumEntity;
import com.forum.forum_backend.repositories.ForumRepository;
import com.forum.forum_backend.services.interfaces.ForumService;
import com.forum.forum_backend.services.interfaces.ThreadService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ForumServiceImpl implements ForumService {

	private final ForumRepository forumRepository;
	private final ThreadService threadService;
	private final UserService userService;

	public ForumServiceImpl(
			ForumRepository forumRepository,
			@Lazy ThreadService threadService,
			UserService userService) {
		this.forumRepository = forumRepository;
		this.threadService = threadService;
		this.userService = userService;
	}

	@Override
	public PaginatedResponse<ForumDto> getMainForumList() {
		List<ForumEntity> forumEntities = forumRepository.findAllByParentForumId(null);

		PaginatedResponse<ForumDto> response = new PaginatedResponse<>();
		response.setResults(new ArrayList<>() {{
			addAll(forumEntities.stream().map(x -> new ForumDto() {{
				setId(x.getId());
				setTitle(x.getTitle());
				setDescription(x.getDescription());
				setCreateTime(x.getCreateTime());

				setForums(
						x.getChildForums()
								.stream().map(ForumServiceImpl.this::mapChildEntityToDto)
								.collect(Collectors.toList())
				);

				setThreads(
						x.getThreadEntities().stream().map(threadService::mapChildEntityToDto).collect(Collectors.toList())
				);
			}}).collect(Collectors.toList()));
		}});

		response.setCount(response.getResults().size());
		return response;
	}

	@Override
	public ForumDto getSubForum(int forumId) throws NotFoundException {
		try {
			ForumEntity forumEntity = forumRepository.getOne(forumId);

			ForumDto forum = new ForumDto();
			forum.setId(forumEntity.getId());
			forum.setTitle(forumEntity.getTitle());
			forum.setDescription(forumEntity.getDescription());
			forum.setCreateTime(forumEntity.getCreateTime());
			forum.setBreadcrump(getBreadcrump(forumEntity));

			if (forumEntity.getParentForum() != null) {
				ForumDto parentForum = new ForumDto();
				parentForum.setId(forumEntity.getParentForum().getId());
				parentForum.setTitle(forumEntity.getParentForum().getTitle());
			}

			forum.setForums(
					forumEntity.getChildForums()
							.stream().map(this::mapChildEntityToDto)
							.collect(Collectors.toList())
			);

			forum.setThreads(
					forumEntity.getThreadEntities()
							.stream().map(threadService::mapChildEntityToDto)
							.collect(Collectors.toList())
			);


			return forum;
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Forum with id = " + forumId + " doesn't exist");
		}
	}

	@Override
	public void addForum(ForumDto forumDto)
			throws UnauthorizedException, NotFoundException {
		Integer parentForumId = forumDto.getParent().getId();
		try {
			ForumEntity forumEntity = new ForumEntity();
			ForumEntity parentForumEntity = null;

			if (parentForumId != null) {
				parentForumEntity = forumRepository.getOne(parentForumId);
				forumEntity.setParentForum(parentForumEntity);
			}

			forumEntity.setTitle(forumDto.getTitle());
			forumEntity.setDescription(forumDto.getDescription());

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			forumEntity.setCreateTime(timestamp);

			if (userService.isUserPermittedToModerate(parentForumEntity)) {
				forumRepository.save(forumEntity);
			} else {
				throw new UnauthorizedException("You have no permission to add forum here");
			}

		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Forum with id: " + parentForumId + " doesn't exist");
		}
	}

	@Override
	public void modifyForum(ForumDto forumDto, int forumId) throws NotFoundException, UnauthorizedException {
		try {
			ForumEntity forumEntity = forumRepository.getOne(forumId);
			if (userService.isUserPermittedToModerate(forumEntity)) {
				if (forumDto.getTitle() != null) {
					forumEntity.setTitle(forumDto.getTitle());
				}
				if (forumDto.getDescription() != null) {
					forumEntity.setDescription(forumDto.getDescription());
				}
				forumRepository.save(forumEntity);
			} else {
				throw new UnauthorizedException("You have no permission to modify this forum");
			}
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Forum with id: " + forumId + " doesn't exist");
		}
	}

	@Override
	public void deleteForum(int forumId) throws NotFoundException, UnauthorizedException {
		try {
			ForumEntity forumEntity = forumRepository.getOne(forumId);

			if (forumEntity.getParentForum() == null) {
				forumRepository.delete(forumEntity);
			} else if (userService.isUserPermittedToModerate(forumEntity.getParentForum())) {
				forumRepository.delete(forumEntity);
			} else {
				throw new UnauthorizedException("You have no permission to delete this forum");
			}
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Forum with id: " + forumId + " doesn't exist");
		}
	}

	@Override
	public List<ForumDto> getBreadcrump(ForumEntity forumEntity) {
		ArrayList<ForumDto> parentList = new ArrayList<>();
		do {
			ForumDto tempForum = new ForumDto();
			tempForum.setTitle(forumEntity.getTitle());
			tempForum.setId(forumEntity.getId());
			parentList.add(tempForum);
			if (forumEntity.getParentForum() != null) {
				forumEntity = forumEntity.getParentForum();
			} else {
				forumEntity = null;
			}
		} while (forumEntity != null);

		ArrayList<ForumDto> breadcrump = new ArrayList<>();

		for (int i = parentList.size() - 1; i >= 0; i--) {
			breadcrump.add(parentList.get(i));
		}
		return breadcrump;
	}

	private ForumDto mapChildEntityToDto(ForumEntity forumEntity) {
		return new ForumDto() {{
			setId(forumEntity.getId());
			setTitle(forumEntity.getTitle());
			setCreateTime(forumEntity.getCreateTime());
			setDescription(forumEntity.getDescription());

			if (!forumEntity.getChildForums().isEmpty()) {
				setChildrenAmount(forumEntity.getChildForums().size());
			} else if (!forumEntity.getThreadEntities().isEmpty()) {
				setChildrenAmount(forumEntity.getThreadEntities().size());
			} else {
				setChildrenAmount(0);
			}
		}};
	}
}
