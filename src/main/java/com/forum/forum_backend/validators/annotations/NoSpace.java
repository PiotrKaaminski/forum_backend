package com.forum.forum_backend.validators.annotations;

import com.forum.forum_backend.validators.NoSpaceValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoSpaceValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoSpace {

	String message() default "Do not use space in username and password";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
