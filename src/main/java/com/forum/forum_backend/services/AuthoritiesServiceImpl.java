package com.forum.forum_backend.services;

import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.dtos.PermissionDto;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.enums.Permission;
import com.forum.forum_backend.exceptions.BadRequestException;
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

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
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
	public List<Permission> getAuthorities() {
		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Permission> authorities = new ArrayList<>();
		if (user.hasAuthority(Permission.ADMIN.name())) {
			authorities.add(Permission.ADMIN);
			authorities.add(Permission.HEAD_MODERATOR);
			authorities.add(Permission.MODERATOR);
		} else if (user.hasAuthority(Permission.HEAD_MODERATOR.name()) || user.hasAuthority(Permission.MODERATOR.name())) {
			authorities.add(Permission.MODERATOR);
		}
		return authorities;
	}

	@Override
	public UserDto assign(PermissionDto permissionDto, int userId) throws NotFoundException, UnauthorizedException, BadRequestException {
		UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if(userPrincipal.getId() == userId) {
			throw new BadRequestException("You cannot modify your own permissions");
		}

		Optional<UserEntity> userToModify = userRepository.findById(userId);
		if (userToModify.isEmpty()) {
			throw new NotFoundException("User with id = " + userId + " doesn't exist");
		}

		if(permissionDto.getName() != null) {
			switch (permissionDto.getName()) {
				case ADMIN:
				case HEAD_MODERATOR:
					if (userPrincipal.hasAuthority(Permission.ADMIN.name())) {
						assignPermission(permissionDto.getName().name(), userToModify.get());
					} else {
						throw new UnauthorizedException("You cannot assign " + permissionDto.getName() + " permission");
					}
					break;
				case MODERATOR:
					if(permissionDto.getForumIdList().isEmpty() || permissionDto.getForumIdList() == null) {
						throw new BadRequestException("Forum id list cannot be empty when assigning MODERATOR permission");
					}
					assignModerator(permissionDto, userToModify.get(), userRepository.getOne(userPrincipal.getId()));
			}
		} else {
			revoke(userToModify.get(), userRepository.getOne(userPrincipal.getId()));
		}

		return userService.getUser(userId);

	}

	private void assignPermission(String permission, UserEntity userToModify) {
		AuthorityEntity authorityEntity = authorityRepository.findByName(permission);
		userToModify.setAuthority(authorityEntity);
		userToModify.setModeratedForums(null);
		userRepository.save(userToModify);
	}

	private void assignModerator(PermissionDto permissionDto, UserEntity userToModify, UserEntity modifier)
			throws NotFoundException, UnauthorizedException {


		if(userToModify.getAuthority() != null) {
			if(userToModify.hasAnyAuthority(Arrays.asList(Permission.ADMIN.name(), Permission.HEAD_MODERATOR.name())) &&
					!modifier.hasAnyAuthority(Collections.singletonList(Permission.ADMIN.name()))) {
				throw new UnauthorizedException("You cannot degrade user with id = " + userToModify.getId() + " from " +
						userToModify.getAuthority().getName() + " to MODERATOR");
			}
		}

		// get all forums listed in dto as forum entity
		List<ForumEntity> forumsToModerate = new ArrayList<>();
		for(Integer forumId : permissionDto.getForumIdList()) {
			Optional<ForumEntity> tempForum = forumRepository.findById(forumId);
			if (tempForum.isEmpty()) {
				throw new NotFoundException("Forum with id = " + forumId + " doesn't exist");
			}
			forumsToModerate.add(tempForum.get());
		}

		// create list of forumEntities to be assigned
		List<ForumEntity> forumsToAssign = new ArrayList<>(forumsToModerate);
		forumsToAssign.removeAll(userToModify.getModeratedForums());

		// check if modifier has permission to assign those forumEntities
		for(ForumEntity forumToAssign : forumsToAssign) {
			System.out.println("forumToAssign id: " + forumToAssign.getId());
			ForumEntity parentForum = null;
			if (forumToAssign.getParentForum() != null) {
				parentForum = forumToAssign.getParentForum();
			}
			if (!userService.isUserPermittedToModerate(parentForum, modifier)) {
				throw new UnauthorizedException("You have no permission to assign moderator to forum with id = " + forumToAssign.getId());
			}
		}

		// create list of forumEntities to be revoked
		List<ForumEntity> forumsToRevoke = new ArrayList<>(userToModify.getModeratedForums());
		forumsToRevoke.removeAll(forumsToModerate);

		// check if modifier has permission to revoke those forumEntities
		for(ForumEntity forumToRevoke : forumsToRevoke) {
			System.out.println("forumToRevoke id: " + forumToRevoke.getId());
			ForumEntity parentForum = null;
			if (forumToRevoke.getParentForum() != null) {
				parentForum = forumToRevoke.getParentForum();
			}
			if (!userService.isUserPermittedToModerate(parentForum, modifier)) {
				throw new UnauthorizedException("You have no permission to revoke moderator from forum with id = " + forumToRevoke.getId());
			}
		}

		// modifier has permisisons to revoke and assign all forums listed in dto
		// save all forum entities listed in dto
		userToModify.setModeratedForums(forumsToModerate);
		AuthorityEntity authorityEntity = authorityRepository.findByName(permissionDto.getName().name());
		userToModify.setAuthority(authorityEntity);
		userRepository.save(userToModify);
	}

	private void revoke(UserEntity userToModify, UserEntity modifier) throws UnauthorizedException {

			if (userToModify.getAuthority() != null &&
					userToModify.hasAnyAuthority(Arrays.asList(Permission.ADMIN.name(), Permission.HEAD_MODERATOR.name()))) {
				if (modifier.hasAnyAuthority(Collections.singletonList(Permission.ADMIN.name()))) {
					userToModify.setAuthority(null);
					userToModify.setModeratedForums(null);
					userRepository.save(userToModify);
				} else {
					throw new UnauthorizedException("You cannot revoke " + userToModify.getAuthority().getName() + " permission");
				}
			} else if (userToModify.getAuthority() != null &&
					userToModify.hasAnyAuthority(Collections.singletonList(Permission.MODERATOR.name()))) {
				for (ForumEntity moderatedForum : userToModify.getModeratedForums()) {
					ForumEntity parentForum = null;
					if (moderatedForum.getParentForum() != null) {
						parentForum = moderatedForum.getParentForum();
					}
					if (!userService.isUserPermittedToModerate(parentForum, modifier)) {
						throw new UnauthorizedException("You have no permission to revoke MODERATOR from forum with id = " + moderatedForum.getId());
					}
				}
				userToModify.setAuthority(null);
				userToModify.setModeratedForums(null);
				userRepository.save(userToModify);
			}

	}

}
