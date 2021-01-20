package com.forum.forum_backend.services;

import com.forum.forum_backend.config.JwtTokenProvider;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.models.AuthorityEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.repositories.AuthorityRepository;
import com.forum.forum_backend.repositories.UserRepository;
import com.forum.forum_backend.services.interfaces.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	private final UserRepository userRepository;
	private final AuthorityRepository authorityRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;

	public AuthenticationServiceImpl(UserRepository userRepository, AuthorityRepository authorityRepository,
									 BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager,
									 JwtTokenProvider jwtTokenProvider) {
		this.userRepository = userRepository;
		this.authorityRepository = authorityRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public String login(UserDto userDto) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						userDto.getUsername(),
						userDto.getPassword()
				)
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		return jwtTokenProvider.generateToken(authentication);
	}

	@Override
	public void registerUser(UserDto userDto){
		UserEntity user = new UserEntity();
		user.setUsername(userDto.getUsername());
		user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

		AuthorityEntity authority = authorityRepository.findByAuthority("USER");
		user.addAuthority(authority);

		userRepository.save(user);

	}

}
