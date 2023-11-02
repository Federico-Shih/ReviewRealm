package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentDifficultyList;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ExistentDifficultyListValidator implements ConstraintValidator<ExistentDifficultyList,Object> {
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (!(o instanceof List)) {
            return false;
        }
        List<Integer> list = (List<Integer>) o;
        for (Integer i : list) {
            if (i == null || i < 0 || !Difficulty.getById(i).isPresent()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void initialize(ExistentDifficultyList constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
