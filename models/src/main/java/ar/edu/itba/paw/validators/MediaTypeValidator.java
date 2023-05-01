package ar.edu.itba.paw.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ar.edu.itba.paw.annotations.ValidMediaType;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class MediaTypeValidator implements ConstraintValidator<ValidMediaType, MultipartFile> {

    private List<String> allowed;

    @Override
    public void initialize(ValidMediaType constraintAnnotation) {
        allowed = Arrays.asList(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        return value == null
                || allowed.contains(MediaType.ALL_VALUE)
                || allowed.contains(value.getContentType());
    }

}