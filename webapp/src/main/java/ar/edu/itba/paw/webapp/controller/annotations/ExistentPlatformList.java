package ar.edu.itba.paw.webapp.controller.annotations;

import ar.edu.itba.paw.webapp.validators.ExistentPlatformListValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistentPlatformListValidator.class)
public @interface ExistentPlatformList {
    String message() default "javax.validation.constraints.ExistentPlatformList.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
