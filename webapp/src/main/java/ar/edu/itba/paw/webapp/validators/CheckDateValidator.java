package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.webapp.annotations.CheckDateFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckDateValidator implements ConstraintValidator<CheckDateFormat, String> {

    private String pattern;

    @Override
    public void initialize(CheckDateFormat constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
        if ( object == null ) {
            return false;
        }

        try {
            Date date = new SimpleDateFormat(pattern).parse(object);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}