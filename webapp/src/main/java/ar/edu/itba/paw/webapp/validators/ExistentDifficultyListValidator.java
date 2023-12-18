package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.enums.Difficulty;
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
        List<String> list = (List<String>) o;
        for (String s : list) {
            if (s == null) {
               return false;
            }else{
                try{
                    Difficulty.valueOf(s.toUpperCase());
                }catch (IllegalArgumentException e){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void initialize(ExistentDifficultyList constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
