package ar.edu.itba.paw.webapp.controller.annotations;

import ar.edu.itba.paw.webapp.validators.MediaTypeValidator;
import org.springframework.http.MediaType;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = MediaTypeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMediaType {

    String[] value() default { MediaType.ALL_VALUE };

    String message() default "javax.validation.constraints.ValidMediaType.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}