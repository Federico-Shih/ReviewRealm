package ar.edu.itba.paw.webapp.controller.annotations;

import ar.edu.itba.paw.webapp.validators.MediaTypeSizeValidator;
import ar.edu.itba.paw.webapp.validators.ValidImageValidator;
import org.springframework.http.MediaType;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidImageValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImage {

    long value() default 128 * 1024 * 1024 ;

    String[] type() default { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE };

    String message() default "javax.validation.constraints.ValidImage.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}