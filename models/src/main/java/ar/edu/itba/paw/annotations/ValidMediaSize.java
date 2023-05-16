package ar.edu.itba.paw.annotations;

import ar.edu.itba.paw.validators.MediaTypeSizeValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MediaTypeSizeValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMediaSize {

    long value() default 128 * 1024 * 1024 ;

    String message() default "{javax.validation.constraints.ValidMediaSize.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}