package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentGenreList;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ExistentGenreListValidator implements ConstraintValidator<ExistentGenreList, Object> {
    private boolean isNullable;
    @Override
    public void initialize(ExistentGenreList constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        isNullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (o == null && isNullable) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }
        List<Integer> list = (List<Integer>) o;
        for (Integer i : list) {
            if (i == null || i < 0 || !Genre.getById(i).isPresent()) {
                return false;
            }
        }
        return true;
    }
}
