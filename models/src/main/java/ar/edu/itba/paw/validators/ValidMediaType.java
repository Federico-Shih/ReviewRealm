package ar.edu.itba.paw.validators;

import org.springframework.http.MediaType;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.util.List;

@Documented
@Constraint(validatedBy = MediaTypeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMediaType {

    String[] value() default { MediaType.ALL_VALUE };

    String message() default "{javax.validation.constraints.ValidMediaType.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}