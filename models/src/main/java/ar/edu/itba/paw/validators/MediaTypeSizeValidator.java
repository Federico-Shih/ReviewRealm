package ar.edu.itba.paw.validators;

import ar.edu.itba.paw.annotations.ValidMediaSize;
import ar.edu.itba.paw.annotations.ValidMediaType;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

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