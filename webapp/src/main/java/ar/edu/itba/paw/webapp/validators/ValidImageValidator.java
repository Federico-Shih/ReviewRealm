package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.webapp.controller.annotations.ValidImage;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.springframework.http.MediaType;

import javax.validation.ConstraintValidator;
import java.util.Arrays;
import java.util.List;

public class ValidImageValidator implements ConstraintValidator<ValidImage, FormDataBodyPart> {

        private long mediaSize;
        private List<String> allowed;

        @Override
        public void initialize(ValidImage constraintAnnotation) {
            mediaSize = constraintAnnotation.value();
            allowed = Arrays.asList(constraintAnnotation.type());
        }

        @Override
        public boolean isValid(FormDataBodyPart value, javax.validation.ConstraintValidatorContext context) {
            return value == null || value.getMediaType() != null && allowed.contains(value.getMediaType().toString())  && value.getContentDisposition().getSize() <= mediaSize;
        }
}
