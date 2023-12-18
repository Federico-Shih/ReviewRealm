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

    public boolean checkReviewAuthorOwnerOrMod(long reviewId) {
        return checkReviewAuthorOwner(reviewId) || checkAccessedUserIdIsMod();
    }

    public boolean checkReviewAuthorOwner(long reviewId) {
        User activeUser = AuthenticationHelper.getLoggedUser(userService);
        if(activeUser == null)
            return false;
        Optional<Review> review = reviewService.getReviewById(reviewId, activeUser.getId());
        return review.isPresent() && Objects.equals(review.get().getAuthor().getId(), activeUser.getId());
    }
    public boolean checkLoggedIsCreatorOfFeedback(long reviewId, long userId){
        User activeUser = AuthenticationHelper.getLoggedUser(userService);
        if(activeUser == null)
            return false;
        Optional<Review> review = reviewService.getReviewById(reviewId, activeUser.getId());
        return review.isPresent() && Objects.equals(userId, activeUser.getId());
    }
    public boolean checkReviewAuthorforFeedback(long reviewId){
        User activeUser = AuthenticationHelper.getLoggedUser(userService);
        if(activeUser == null)
            return false;
        Optional<Review> review = reviewService.getReviewById(reviewId, activeUser.getId());
        return review.isPresent() && !Objects.equals(review.get().getAuthor().getId(), activeUser.getId());
    }
    public boolean checkGameAuthorReviewed(long gameId) {
        User activeUser = AuthenticationHelper.getLoggedUser(userService);
        if(activeUser == null)
            return false;
        return !reviewService.getReviewOfUserForGame(activeUser.getId(), gameId).isPresent();
    }

    public boolean checkAccessedUserIdIsUser(long id) {
        User activeUser = AuthenticationHelper.getLoggedUser(userService);
        if(activeUser == null)
            return false;
        return Objects.equals(activeUser.getId(), id);
    }
    public boolean checkSuggestedFilterIsModerator(boolean isSuggested){
        User activeUser = AuthenticationHelper.getLoggedUser(userService);
        if(activeUser == null || !activeUser.isModerator())
            return !isSuggested;
        return true;
    }

    public boolean checkAccessedUserIdIsMod() {
        User activeUser = AuthenticationHelper.getLoggedUser(userService);
        if(activeUser == null)
            return false;
        return activeUser.isModerator();
    }

    public boolean checkUserIsNotAuthor(long reviewId) {
        return !checkReviewAuthorOwner(reviewId);
    }
}
