package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.annotations.ExistentEmail;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistentEmailValidator implements ConstraintValidator<ExistentEmail, String> {
    private final UserService userService;

    @Autowired
    public ExistentEmailValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(ExistentEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return userService.getUserByEmail(s).isPresent();
    }
}
