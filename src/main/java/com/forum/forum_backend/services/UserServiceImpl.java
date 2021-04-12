package com.forum.forum_backend.services;

import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.dtos.PaginatedResponse;
import com.forum.forum_backend.dtos.PermissionDto;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.enums.Permission;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.models.ForumEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.repositories.UserRepository;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public UserServiceImpl(UserRepository userRepository, @Lazy BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByUsername(username);
		return createUserPrincipal(userEntity);
	}

	public UserPrincipal createUserPrincipal(int userId) {
		UserEntity user = getUserById(userId);
		return createUserPrincipal(user);
	}

	private UserPrincipal createUserPrincipal(UserEntity userEntity) {
		List<GrantedAuthority> authorities = new ArrayList<>();

		if (userEntity.getAuthority() != null) {
			authorities.add(new SimpleGrantedAuthority(userEntity.getAuthority().getName().toUpperCase()));
		}

		return new UserPrincipal(
				userEntity.getId(),
				userEntity.getUsername(),
				userEntity.getPassword(),
				authorities
		);

	}

	public UserEntity getUserById(int userId) {
		UserEntity user = null;
		try {
			user = userRepository.getOne(userId);
		} catch (EntityNotFoundException e) {
			System.out.println("UserService: user with given id not found");
		}
		return user;
	}

	@Override
	public boolean isUserAnAuthor(UserEntity entryAuthor) {

		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
			return false;
		}

		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = user.getId();

		return entryAuthor.getId() == userId;
	}

	@Override
	public boolean isUserPermittedToModerate(ForumEntity forumEntity, UserEntity user) {
		if (user.getAuthority() == null) {
			return false;
		}

		if (user.getAuthority().getName().equals(Permission.ADMIN.name()) ||
			user.getAuthority().getName().equals(Permission.HEAD_MODERATOR.name())) {
			return true;
		}

		while (forumEntity != null){
			if (forumEntity.isUserModerator(user)) {
				return true;
			}

			if (forumEntity.getParentForum() != null) {
				forumEntity = forumEntity.getParentForum();
			} else {
				forumEntity = null;
			}
		}

		return false;
	}

	@Override
	public boolean isUserPermittedToModerate(ForumEntity forumEntity) {
		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
			return false;
		}

		UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity user = getUserById(userPrincipal.getId());

		return isUserPermittedToModerate(forumEntity, user);
	}

	@Override
	public void addUser(UserDto userDto){
		UserEntity user = new UserEntity();
		user.setUsername(userDto.getUsername());
		user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
		user.setJoinTime(new Timestamp(System.currentTimeMillis()));

		userRepository.save(user);

	}

	@Override
	public UserDto myAccountInfo() {
		UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity userEntity = getUserById(userPrincipal.getId());
		UserDto user = new UserDto();

		user.setId(userEntity.getId());
		user.setUsername(userEntity.getUsername());
		//email not implemented, email is now hard-coded to UserDto
		user.setJoinTime(userEntity.getJoinTime());
		user.setAuthority(getPermissionFromUserEntity(userEntity));

		return user;
	}

	@Override
	public PaginatedResponse<UserDto> getUsers(String username, int size, int page) {
		Pageable pageable = PageRequest.of(page, size);
		Page<UserEntity> userEntityPage = userRepository.findByUsernameStartsWith(username, pageable);

		PaginatedResponse<UserDto> response = new PaginatedResponse<>();
		response.setResults(new ArrayList<>() {{
			addAll(userEntityPage.stream().map(x -> new UserDto() {{
				setId(x.getId());
				setUsername(x.getUsername());
				setJoinTime(x.getJoinTime());
				setAuthority(getPermissionFromUserEntity(x));
			}}).collect(Collectors.toList()));
		}});
		response.setCount((int) userEntityPage.getTotalElements());

		return response;
	}

	@Override
	public UserDto getUser(int userId) throws NotFoundException {
		try {
			UserEntity userEntity = userRepository.getOne(userId);

			UserDto user = new UserDto();

			user.setId(userEntity.getId());
			user.setUsername(userEntity.getUsername());
			//email not implemented, email is now hard-coded to UserDto
			user.setJoinTime(userEntity.getJoinTime());
			user.setAuthority(getPermissionFromUserEntity(userEntity));

			return user;
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("User with id = " + userId + " doesn't exist");
		}
	}

	private PermissionDto getPermissionFromUserEntity(UserEntity userEntity) {
		PermissionDto permissionDto = new PermissionDto();
		if (userEntity.getAuthority() != null) {
			switch (userEntity.getAuthority().getName()) {
				case "ADMIN":
					permissionDto.setName(Permission.ADMIN);
					break;
				case "HEAD_MODERATOR":
					permissionDto.setName(Permission.HEAD_MODERATOR);
					break;
				case "MODERATOR":
					permissionDto.setName(Permission.MODERATOR);
					for (ForumEntity forumEntity : userEntity.getModeratedForums()) {
						permissionDto.addForumId(forumEntity.getId());
					}
					break;
			}
		}
		return permissionDto;
	}

	@Override
	public List<UserEntity> setLikes (List<UserEntity> userLikes, boolean liked) {
		UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserEntity userEntity = getUserById(userPrincipal.getId());

		if (liked && !userLikes.contains(userEntity)) {
			userLikes.add(userEntity);
		}

		if (!liked) {
			userLikes.remove(userEntity);
		}

		return userLikes;
	}
}











