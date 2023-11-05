package ar.edu.itba.paw.webapp.controller.annotations;


import ar.edu.itba.paw.webapp.validators.ExistentGenreListValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistentGenreListValidator.class)
public @interface ExistentGenreList {
    String message() default "javax.validation.constraints.ExistentGenreList.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean nullable() default false;
}
