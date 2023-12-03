package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.webapp.controller.annotations.CheckDateFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.DateFormat;
import java.text.ParseException;
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
            return true;
        }
        DateFormat sdf = new SimpleDateFormat(this.pattern);
        sdf.setLenient(false);
        try {
            sdf.parse(object);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}