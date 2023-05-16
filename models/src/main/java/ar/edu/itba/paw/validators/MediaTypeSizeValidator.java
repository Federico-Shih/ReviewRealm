package ar.edu.itba.paw.validators;

import ar.edu.itba.paw.annotations.ValidMediaSize;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MediaTypeSizeValidator implements ConstraintValidator<ValidMediaSize, MultipartFile> {

    private long mediaSize;

    @Override
    public void initialize(ValidMediaSize constraintAnnotation) {
        mediaSize = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        return value.getSize() <= mediaSize;
    }

}