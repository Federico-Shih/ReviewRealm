package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentGameId;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistentGameIdValidator implements ConstraintValidator<ExistentGameId, Long> {
    private final GameService gameService;
    private boolean optional;

    @Autowired
    public ExistentGameIdValidator(GameService gameService) {
        this.gameService = gameService;
    }


    @Override
    public void initialize(ExistentGameId constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        optional = constraintAnnotation.optional();
    }

    @Override
    public boolean isValid(Long aLong, ConstraintValidatorContext constraintValidatorContext) {
        if (optional && aLong == null) return true;
        if (aLong == null) return false;
        return gameService.getGameById(aLong,null).isPresent();
    }
}
