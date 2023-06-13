package ar.edu.itba.paw.webapp.annotations;

import ar.edu.itba.paw.webapp.validators.ExistentEmailValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistentEmailValidator.class)
public @interface ExistentEmail {
    String message() default "{javax.validation.constraints.ExistentEmail.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
