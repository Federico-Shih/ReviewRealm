package ar.edu.itba.paw.webapp.controller.annotations;


import ar.edu.itba.paw.webapp.validators.ExistentGameIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistentGameIdValidator.class)
public @interface ExistentGameId {
    String message() default "javax.validation.constraints.ExistentGameId.message";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean optional() default false;
}
