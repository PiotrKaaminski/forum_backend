package com.forum.forum_backend.validators.annotations;

import com.forum.forum_backend.validators.UsernameUniqueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UsernameUniqueValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UsernameUnique {
	String message() default "User with given username already exists";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
