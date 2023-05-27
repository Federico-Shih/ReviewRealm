package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.Optional;

@Component
public class AccessControl {
    private final UserService userService;
    private final ReviewService reviewService;

    @Autowired
    public AccessControl(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    public boolean checkReviewAuthorOwner(Long reviewId) {
        User activeUser = AuthenticationHelper.getLoggedUser(userService);
        Optional<Review> review = reviewService.getReviewById(reviewId, activeUser);
        return review.filter(value -> Objects.equals(value.getAuthor().getId(), activeUser.getId())).isPresent();
    }
}