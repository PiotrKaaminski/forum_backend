package com.forum.forum_backend.validators;

import com.forum.forum_backend.validators.annotations.NoSpace;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NoSpaceValidator implements ConstraintValidator<NoSpace, String> {

	@Override
	public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
		return !s.contains(" ");
	}

	@Override
	public void initialize(NoSpace constraintAnnotation) {

	}
}
