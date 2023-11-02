package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentPlatformList;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ExistentPlatformListValidator implements ConstraintValidator<ExistentPlatformList,Object> {
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (!(o instanceof List)) {
            return false;
        }
        List<Integer> list = (List<Integer>) o;
        for (Integer i : list) {
            if (i == null || i < 0 || !Platform.getById(i).isPresent()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void initialize(ExistentPlatformList constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
