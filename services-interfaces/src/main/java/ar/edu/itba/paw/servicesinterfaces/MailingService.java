package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.models.ExpirationToken;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;

import java.util.Map;

public interface MailingService {
    void sendChangePasswordEmail(ExpirationToken token, User user);

    void sendValidationTokenEmail(ExpirationToken token, User user);

    void sendReviewDeletedEmail(Game game, User user);

    void sendReviewCreatedEmail(Review createdReview, User author, User follower);
}