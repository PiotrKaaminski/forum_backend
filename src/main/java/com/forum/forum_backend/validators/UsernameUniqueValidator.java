package com.forum.forum_backend.validators;

import com.forum.forum_backend.repositories.UserRepository;
import com.forum.forum_backend.validators.annotations.UsernameUnique;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameUniqueValidator implements ConstraintValidator<UsernameUnique, String> {

	@Autowired
	private UserRepository userRepository;

	@Override
	public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
		return !userRepository.existsByUsername(username);
	}

	@Override
	public void initialize(UsernameUnique constraintAnnotation) {

	}
}
