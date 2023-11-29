package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentGameId;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistentGameIdValidator implements ConstraintValidator<ExistentGameId, Long> {
    private final GameService gameService;
    private final UserService userService;
    private boolean optional;

    @Autowired
    public ExistentGameIdValidator(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
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
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
        Long userId = loggedUser != null ? loggedUser.getId() : null;
        return gameService.getGameById(aLong,userId).isPresent();
    }
}
