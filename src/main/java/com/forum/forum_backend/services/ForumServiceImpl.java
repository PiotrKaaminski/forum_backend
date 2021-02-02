package com.forum.forum_backend.services;

import com.forum.forum_backend.dtos.ForumDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.ForumEntity;
import com.forum.forum_backend.repositories.ForumRepository;
import com.forum.forum_backend.services.interfaces.ForumService;
import com.forum.forum_backend.services.interfaces.ThreadService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ForumServiceImpl implements ForumService {

	private final ForumRepository forumRepository;
	private final ThreadService threadService;
	private final UserService userService;

	public ForumServiceImpl(ForumRepository forumRepository, ThreadService threadService, UserService userService) {
		this.forumRepository = forumRepository;
		this.threadService = threadService;
		this.userService = userService;
	}

	@Override
	public List<ForumDto> getMainForumList() {
		List<ForumEntity> forumEntities = forumRepository.findAllByParentForumId(null);

		return new ArrayList<>() {{
			addAll(forumEntities.stream().map(x -> new ForumDto() {{
				setId(x.getId());
				setTitle(x.getTitle());
				if (!x.getChildForums().isEmpty()) {
					setChildForums(
							x.getChildForums()
									.stream().map(ForumServiceImpl.this::mapChildEntityToDto)
									.collect(Collectors.toList())
					);
				} else if (!x.getThreadEntities().isEmpty()) {
					setThreads(
							x.getThreadEntities()
									.stream().map(threadService::mapChildEntityToDto)
									.collect(Collectors.toList())
					);
				}
			}}).collect(Collectors.toList()));
		}};
	}

	@Override
	public ForumDto getSubForum(int forumId) throws NotFoundException {
		try {
			ForumEntity forumEntity = forumRepository.getOne(forumId);

			ForumDto forum = new ForumDto();
			forum.setId(forumEntity.getId());
			forum.setTitle(forumEntity.getTitle());

			if (forumEntity.getParentForum() != null) {
				forum.setParentId(forumEntity.getParentForum().getId());
			}

			if (!forumEntity.getChildForums().isEmpty()) {
				forum.setChildForums(
						forumEntity.getChildForums()
								.stream().map(this::mapChildEntityToDto)
								.collect(Collectors.toList())
				);

			} else if (!forumEntity.getThreadEntities().isEmpty()) {
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
	public void addMainForum(ForumDto forumDto){
		ForumEntity forum = new ForumEntity();
		forum.setTitle(forumDto.getTitle());
		forumRepository.save(forum);
	}

	@Override
	public void addSubForum(ForumDto forumDto, int parentForumId)
			throws UnauthorizedException, NotFoundException {
		try {
			ForumEntity parentForumEntity = forumRepository.getOne(parentForumId);
			ForumEntity forumEntity = new ForumEntity();
			forumEntity.setTitle(forumDto.getTitle());
			forumEntity.setParentForum(parentForumEntity);

			if (parentForumEntity.getThreadEntities().isEmpty()) {
				if (userService.isUserPermittedToModerate(parentForumEntity)) {
					forumRepository.save(forumEntity);
				} else {
					throw new UnauthorizedException("You have no permission to add forum here");
				}
			} else {
				throw new UnauthorizedException("Forum already contains threads");
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
				forumEntity.setTitle(forumDto.getTitle());
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

	private ForumDto mapChildEntityToDto(ForumEntity forumEntity) {
		return new ForumDto() {{
			setId(forumEntity.getId());
			setTitle(forumEntity.getTitle());

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
