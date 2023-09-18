package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.annotations.ExistentEmail;
import ar.edu.itba.paw.webapp.annotations.ExistentUserId;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistentUserIdValidator implements ConstraintValidator<ExistentUserId, Long> {
    private final UserService userService;

    @Autowired
    public ExistentUserIdValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(ExistentUserId constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        if (id == null) return false;
        return userService.getUserById(id).isPresent();
    }
}
