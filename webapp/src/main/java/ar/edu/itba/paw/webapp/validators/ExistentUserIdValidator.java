package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentUserId;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistentUserIdValidator implements ConstraintValidator<ExistentUserId, Long> {
    private final UserService userService;
    private boolean optional;

    @Autowired
    public ExistentUserIdValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(ExistentUserId constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        optional = constraintAnnotation.optional();
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        if (optional && id == null) return true;
        if (id == null) return false;
        return userService.getUserById(id).isPresent();
    }
}
