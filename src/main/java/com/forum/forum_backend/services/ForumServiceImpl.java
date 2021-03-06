package com.forum.forum_backend.services;

import com.forum.forum_backend.dtos.ForumDto;
import com.forum.forum_backend.dtos.PaginatedResponse;
import com.forum.forum_backend.dtos.ThreadDto;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.ForumEntity;
import com.forum.forum_backend.models.ThreadEntity;
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
import java.util.Optional;
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
				setCanModerate(userService.isUserPermittedToModerate(null));

				setForums(
						x.getChildForums()
								.stream().map(ForumServiceImpl.this::mapChildEntityToDto)
								.collect(Collectors.toList())
				);

				setThreads(null);
			}}).collect(Collectors.toList()));
		}});

		response.setCount(response.getResults().size());
		return response;
	}

	@Override
	public ForumDto getSubForum(int forumId, boolean getThreads) throws NotFoundException {
		try {
			ForumEntity forumEntity = forumRepository.getOne(forumId);

			ForumDto forum = new ForumDto();
			forum.setId(forumEntity.getId());
			forum.setTitle(forumEntity.getTitle());
			forum.setDescription(forumEntity.getDescription());
			forum.setCreateTime(forumEntity.getCreateTime());
			forum.setBreadcrumb(getBreadcrumb(forumEntity));
			forum.setCanModerate(userService.isUserPermittedToModerate(forumEntity.getParentForum()));

			if (forumEntity.getParentForum() != null) {
				ForumDto parentForum = new ForumDto();
				parentForum.setId(forumEntity.getParentForum().getId());
				parentForum.setTitle(forumEntity.getParentForum().getTitle());
				forum.setParent(parentForum);
			}

			forum.setForums(
					forumEntity.getChildForums()
							.stream().map(this::mapChildEntityToDto)
							.collect(Collectors.toList())
			);

			if (getThreads) {
				forum.setThreads(
						forumEntity.getThreadEntities()
								.stream().map(threadService::mapChildEntityToDto)
								.collect(Collectors.toList())
				);
			}


			return forum;
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Forum with id = " + forumId + " doesn't exist");
		}
	}

	@Override
	public ForumDto addForum(ForumDto forumDto) throws UnauthorizedException, NotFoundException {
		ForumEntity forumEntity = new ForumEntity();
		ForumEntity parentForum = null;

		if (forumDto.getParent() != null && forumDto.getParent().getId() != null) {
			int parentForumId = forumDto.getParent().getId();
			Optional<ForumEntity> parentForumEntity = forumRepository.findById(parentForumId);

			if (parentForumEntity.isEmpty()) {
				throw new NotFoundException("Forum with id = " + parentForumId + " doesn't exist");
			}
			parentForum = parentForumEntity.get();
			forumEntity.setParentForum(parentForum);
		}


		forumEntity.setTitle(forumDto.getTitle());
		forumEntity.setDescription(forumDto.getDescription());

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		forumEntity.setCreateTime(timestamp);

		if (userService.isUserPermittedToModerate(parentForum)) {
			int forumId = forumRepository.save(forumEntity).getId();
			return getSubForum(forumId, true);
		} else {
			throw new UnauthorizedException("You have no permission to add forum here");
		}
	}

	@Override
	public ForumDto modifyForum(ForumDto forumDto, int forumId) throws NotFoundException, UnauthorizedException {
		try {
			ForumEntity forumEntity = forumRepository.getOne(forumId);
			if (userService.isUserPermittedToModerate(forumEntity)) {

				forumEntity.setTitle(forumDto.getTitle());
				forumEntity.setDescription(forumDto.getDescription());

				forumRepository.save(forumEntity);
				return getSubForum(forumId, true);
			} else {
				throw new UnauthorizedException("You have no permission to modify this forum");
			}
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Forum with id = " + forumId + " doesn't exist");
		}
	}

	@Override
	public void deleteForum(int forumId) throws NotFoundException, UnauthorizedException {
		try {
			ForumEntity forumEntity = forumRepository.getOne(forumId);
			ForumEntity parentForumEntity = null;
			if (forumEntity.getParentForum() != null) {
				parentForumEntity = forumEntity.getParentForum();
			}

			if (userService.isUserPermittedToModerate(parentForumEntity)) {
				forumRepository.delete(forumEntity);
			} else {
				throw new UnauthorizedException("You have no permission to delete this forum");
			}
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Forum with id = " + forumId + " doesn't exist");
		}
	}

	@Override
	public List<ForumDto> getBreadcrumb(ForumEntity forumEntity) {
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

		ArrayList<ForumDto> breadcrumb = new ArrayList<>();

		for (int i = parentList.size() - 1; i >= 0; i--) {
			breadcrumb.add(parentList.get(i));
		}
		return breadcrumb;
	}

	private ForumDto mapChildEntityToDto(ForumEntity forumEntity) {
		return new ForumDto() {{
			setId(forumEntity.getId());
			setTitle(forumEntity.getTitle());
			setCreateTime(forumEntity.getCreateTime());
			setDescription(forumEntity.getDescription());
			setCanModerate(userService.isUserPermittedToModerate(forumEntity.getParentForum()));

			if (forumEntity.getThreadEntities().size() > 0) {
				ThreadDto latestThread = new ThreadDto();
				ThreadEntity threadEntity = forumEntity.getThreadEntities().get(forumEntity.getThreadEntities().size() - 1);
				latestThread.setId(threadEntity.getId());
				latestThread.setTitle(threadEntity.getTitle());
				latestThread.setCreateTime(threadEntity.getCreateTime());

				UserDto creator = new UserDto();
				creator.setId(threadEntity.getUser().getId());
				creator.setUsername(threadEntity.getUser().getUsername());
				latestThread.setCreator(creator);
				setLatestThread(latestThread);
			}

			setThreadsAmount(forumEntity.getThreadEntities().size());

		}};
	}
}
