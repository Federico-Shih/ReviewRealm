package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentReviewId;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistentReviewIdValidator implements ConstraintValidator<ExistentReviewId, Long> {
    private final ReviewService reviewService;

    @Autowired
    public ExistentReviewIdValidator(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    @Override
    public void initialize(ExistentReviewId constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long aLong, ConstraintValidatorContext constraintValidatorContext) {
        return reviewService.getReviewById(aLong,null).isPresent();
    }
}
