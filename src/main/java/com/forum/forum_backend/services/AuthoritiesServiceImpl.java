package com.forum.forum_backend.services;

import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.dtos.PermissionDto;
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

import javax.persistence.EntityNotFoundException;
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
	public void assign(PermissionDto permissionDto, int userId) throws NotFoundException, UnauthorizedException, BadRequestException {
		UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if(userPrincipal.getId() == userId) {
			throw new BadRequestException("You cannot modify your own permissions");
		}

		if(permissionDto.getName() != null) {
			switch (permissionDto.getName()) {
				case ADMIN, HEAD_MODERATOR -> {
					if (userPrincipal.hasAuthority(Permission.ADMIN.name())) {
						assignPermission(permissionDto.getName().name(), userId);
					} else {
						throw new UnauthorizedException("You cannot assign " + permissionDto.getName() + " permission");
					}
				}
				case MODERATOR -> {
					if(permissionDto.getForumIdList().isEmpty() || permissionDto.getForumIdList() == null) {
						throw new BadRequestException("Forum id list cannot be empty when assigning MODERATOR permission");
					}
					assignModerator(permissionDto, userId, userRepository.getOne(userPrincipal.getId()));
				}
			}
		} else {
			revoke(userId, userRepository.getOne(userPrincipal.getId()));
		}


	}

	private void assignPermission(String permission, int userId) throws NotFoundException {
		try {
			UserEntity userEntity = userRepository.getOne(userId);
			AuthorityEntity authorityEntity = authorityRepository.findByName(permission);
			userEntity.setAuthority(authorityEntity);
			userEntity.setModeratedForums(null);
			userRepository.save(userEntity);

		} catch (EntityNotFoundException e) {
			throw new NotFoundException("User with id = " + userId + " doesn't exist");
		}
	}

	private void assignModerator(PermissionDto permissionDto, int userId, UserEntity modifier) throws NotFoundException, UnauthorizedException, BadRequestException {
		try {
			UserEntity userToModify = userRepository.getOne(userId);

			if(userToModify.getAuthority() != null) {
				if(userToModify.hasAnyAuthority(Arrays.asList(Permission.ADMIN.name(), Permission.HEAD_MODERATOR.name())) &&
						!modifier.hasAnyAuthority(Collections.singletonList(Permission.ADMIN.name()))) {
					throw new UnauthorizedException("You cannot degrade user with id = " + userId + " from " +
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
				if (forumToAssign.isUserModerator(userToModify)) {
					throw new BadRequestException("User with id = " + userToModify.getId() + " is already moderator of forum with id = " + forumToAssign.getId());
				}
			}

			// create list of forumEntites to be revoked
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

		} catch (EntityNotFoundException e) {
			throw new NotFoundException("User with id = " + userId + " doesn't exist");
		}
	}

	private void revoke(int userId, UserEntity modifier) throws NotFoundException, UnauthorizedException, BadRequestException {
		try {
			UserEntity userToRevoke = userRepository.getOne(userId);

			if (userToRevoke.getAuthority() == null) {
				throw new BadRequestException("User with id = " + userId + " has no permission to revoke");
			} else if (userToRevoke.hasAnyAuthority(Arrays.asList(Permission.ADMIN.name(), Permission.HEAD_MODERATOR.name()))) {
				if (modifier.hasAnyAuthority(Collections.singletonList(Permission.ADMIN.name()))) {
					userToRevoke.setAuthority(null);
					userToRevoke.setModeratedForums(null);
					userRepository.save(userToRevoke);
				} else {
					throw new UnauthorizedException("You cannot revoke " + userToRevoke.getAuthority().getName() + " permission");
				}
			} else if (userToRevoke.hasAnyAuthority(Collections.singletonList(Permission.MODERATOR.name()))) {
				for (ForumEntity moderatedForum : userToRevoke.getModeratedForums()) {
					ForumEntity parentForum = null;
					if (moderatedForum.getParentForum() != null) {
						parentForum = moderatedForum.getParentForum();
					}
					if (!userService.isUserPermittedToModerate(parentForum, modifier)) {
						throw new UnauthorizedException("You have no permission revoke MODERATOR form forum with id = " + moderatedForum.getId());
					}
				}
				userToRevoke.setAuthority(null);
				userToRevoke.setModeratedForums(null);
				userRepository.save(userToRevoke);
			}
		} catch (EntityNotFoundException e) {
			throw new NotFoundException("User wit id = " + userId + " doesn't exist");
		}
	}

}
