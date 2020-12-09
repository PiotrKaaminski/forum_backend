package com.forum.forum_backend.services;

import com.forum.forum_backend.config.JwtTokenProvider;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.exceptions.InvalidCredentialsException;
import com.forum.forum_backend.models.AuthorityEntity;
import com.forum.forum_backend.models.UserEntity;
import com.forum.forum_backend.repositories.AuthorityRepository;
import com.forum.forum_backend.repositories.UserRepository;
import com.forum.forum_backend.services.interfaces.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final AuthorityRepository authorityRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;

	public AuthServiceImpl(UserRepository userRepository, AuthorityRepository authorityRepository,
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
	public void registerUser(UserDto userDto) throws InvalidCredentialsException {
		validateRegisterCredentials(userDto);

		UserEntity user = new UserEntity();
		user.setUsername(userDto.getUsername());
		user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

		AuthorityEntity authority = authorityRepository.findByAuthority("USER");
		user.addAuthority(authority);

		userRepository.save(user);

	}

	private void validateRegisterCredentials(UserDto userDto) throws InvalidCredentialsException{
		if (userDto.getUsername() == null || userDto.getUsername().equals("")) {
			throw new InvalidCredentialsException("Username cannot be empty");
		} else if (userDto.getUsername().length() < 5) {
			throw new InvalidCredentialsException("Username too short");
		} else if (userDto.getUsername().length() > 25) {
			throw new InvalidCredentialsException("Username too long");
		} else if (userDto.getPassword() == null || userDto.getPassword().equals("")) {
			throw new InvalidCredentialsException("Password cannot be empty");
		} else if (userDto.getPassword().length() < 5) {
			throw new InvalidCredentialsException("Password too short");
		} else if (userDto.getPassword().length() > 30) {
			throw new InvalidCredentialsException("Password too long");
		} else if (userRepository.existsByUsername(userDto.getUsername())) {
			throw new InvalidCredentialsException("User with given username already exists");
		}
	}
}
