package com.forum.forum_backend.services;

import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.dtos.PaginatedResponse;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.enums.Permission;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.models.AuthorityEntity;
import com.forum.forum_backend.models.ForumEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.repositories.AuthorityRepository;
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
import java.util.Collection;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final AuthorityRepository authorityRepository;

	public UserServiceImpl(UserRepository userRepository, @Lazy BCryptPasswordEncoder bCryptPasswordEncoder, AuthorityRepository authorityRepository) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.authorityRepository = authorityRepository;
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
		Collection<GrantedAuthority> authorities = new ArrayList<>();

		if (userEntity.getAuthorities() != null) {
			userEntity.getAuthorities().forEach(r -> authorities.add(new SimpleGrantedAuthority(r.getAuthority().toUpperCase())));
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
	public boolean isUserPermittedToModerate(ForumEntity forumEntity) {

		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
			return false;
		}

		UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


		if (userPrincipal.hasAuthority(Permission.ADMIN.name()) || userPrincipal.hasAuthority(Permission.HEAD_MODERATOR.name())) {
			return true;
		}

		UserEntity user = getUserById(userPrincipal.getId());

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
	public void addUser(UserDto userDto){
		UserEntity user = new UserEntity();
		user.setUsername(userDto.getUsername());
		user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
		user.setJoinTime(new Timestamp(System.currentTimeMillis()));

		AuthorityEntity authority = authorityRepository.findByAuthority("USER");
		user.addAuthority(authority);

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

		for (GrantedAuthority authority : userPrincipal.getAuthorities()) {
			user.addAuthority(authority.getAuthority());
		}
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
				for (AuthorityEntity authority : x.getAuthorities()) {
					addAuthority(authority.getAuthority());
				}
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

			for (AuthorityEntity authority : userEntity.getAuthorities()) {
				user.addAuthority(authority.getAuthority());
			}
			return user;
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("User with id = " + userId + " doesn't exist");
		}
	}
}











