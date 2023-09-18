package ar.edu.itba.paw.webapp.controller.annotations;

import ar.edu.itba.paw.webapp.validators.CheckDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CheckDateValidator.class)
@Documented
public @interface CheckDateFormat {

    String message() default "javax.validation.constraints.CheckDateFormat.message";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String pattern();

}
