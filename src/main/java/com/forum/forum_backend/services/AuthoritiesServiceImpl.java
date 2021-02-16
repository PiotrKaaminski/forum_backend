package com.forum.forum_backend.services;

import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.dtos.PermissionDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;
import com.forum.forum_backend.models.AuthorityEntity;
import com.forum.forum_backend.models.ForumEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.repositories.AuthorityRepository;
import com.forum.forum_backend.repositories.ForumRepository;
import com.forum.forum_backend.repositories.UserRepository;
import com.forum.forum_backend.services.interfaces.AuthoritiesService;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthoritiesServiceImpl implements AuthoritiesService {

	private final AuthorityRepository  authorityRepository;
	private final UserService userService;
	private final UserRepository userRepository;
	private final ForumRepository forumRepository;

	public AuthoritiesServiceImpl(
			AuthorityRepository authorityRepository,
			UserService userService,
			UserRepository userRepository,
			ForumRepository forumRepository) {
		this.authorityRepository = authorityRepository;
		this.userService = userService;
		this.userRepository = userRepository;
		this.forumRepository = forumRepository;
	}

	@Override
	public List<String> getAuthorities() {
		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<String> authorities = new ArrayList<>();
		if (user.hasAuthority("ADMIN")) {
			authorities.add("ADMIN");
			authorities.add("HEAD_MODERATOR");
			authorities.add("MODERATOR");
		} else if (user.hasAuthority("HEAD_MODERATOR") || user.hasAuthority("MODERATOR")) {
			authorities.add("MODERATOR");
		}
		return authorities;
	}

	@Override
	public void assignPermission(PermissionDto permissionDto, int userId) throws NotFoundException, UnauthorizedException {

		if (permissionDto.getName().equals("MODERATOR")) {
			assignModerator(permissionDto, userId);
		} else if (permissionDto.getName().equals("ADMIN") || permissionDto.getName().equals("HEAD_MODERATOR")) {
			assign(permissionDto.getName(), userId);
		}

	}

	private void assignModerator(PermissionDto permission, int userId) throws NotFoundException, UnauthorizedException {
		UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		try {
			ForumEntity forumEntity = forumRepository.getOne(permission.getForumId());
			ForumEntity parentForumEntity = null;

			if (forumEntity.getParentForum() != null) {
				parentForumEntity = forumEntity.getParentForum();
			}

			if (userPrincipal.hasAuthority("MODERATOR") && !userService.isUserPermittedToModerate(parentForumEntity)) {
				throw new UnauthorizedException("You have no permission to assign moderator to this forum");
			}

			try {
				UserEntity userEntity = userRepository.getOne(userId);
				AuthorityEntity authority = authorityRepository.findByAuthority(permission.getName());
				userEntity.addAuthority(authority);
				forumEntity.addModerator(userEntity);
				forumRepository.save(forumEntity);
			} catch (EntityNotFoundException ex) {
				throw new NotFoundException("User with id = " + userId + " doesn't exist");
			}
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Forum with id = " + permission.getForumId() + " doesn't exist");
		}
	}

	private void assign(String permission, int userId) throws UnauthorizedException, NotFoundException {
		UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (!userPrincipal.hasAuthority("ADMIN")) {
			throw new UnauthorizedException("You have to be admin to assign " + permission + " permission");
		}

		try {
			AuthorityEntity authority = authorityRepository.findByAuthority(permission);

			try {
				UserEntity user = userRepository.getOne(userId);
				user.addAuthority(authority);
				userRepository.save(user);
			} catch (EntityNotFoundException ex) {
				throw new NotFoundException("User with id = " + userId + " doesn't exist");
			}
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Authority " + permission + " doesn't exist");
		}

	}

}
