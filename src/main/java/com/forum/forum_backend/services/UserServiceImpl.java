package com.forum.forum_backend.services;

import com.forum.forum_backend.config.UserPrincipal;
import com.forum.forum_backend.models.ForumEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.repositories.UserRepository;
import com.forum.forum_backend.services.interfaces.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;

@Transactional
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	private final UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
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
		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = user.getId();

		return entryAuthor.getId() == userId;
	}

	@Override
	public boolean isUserPermittedToModerate(ForumEntity forumEntity) {
		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (user.hasAuthority("ADMIN") || user.hasAuthority("HEAD_MODERATOR")) {
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
}











