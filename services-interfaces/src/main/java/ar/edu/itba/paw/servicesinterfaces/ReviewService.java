package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.dtos.SaveReviewDTO;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.ReviewFilter;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
import ar.edu.itba.paw.dtos.searching.ReviewSearchFilter;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.enums.ReviewFeedback;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review createReview(String title,
                        String content,
                        Integer rating,
                        User author,
                        Game reviewedGame,
                        Difficulty difficulty,
                        Double gameLength,
                        Platform platform,
                        Boolean completed,
                        Boolean replayable);

    int updateReview(Long id,
                     String title,
                     String content,
                     Integer rating,
                     Difficulty difficulty,
                     Double gameLength,
                     Platform platform,
                     Boolean completed,
                     Boolean replayable);

    Optional<Review> getReviewById(Long id, User activeUser);

    List<Review> getUserReviews(long userId,User activeUser);

    Paginated<Review> searchReviews(Page page, ReviewSearchFilter filter, Ordering<ReviewOrderCriteria> ordering, User activeUser);

    List<Review> getReviewsFromFollowingByUser(Long userId, Integer size); //No es necesario Active User porque es el mismo que el que me pasan
    boolean deleteReviewById(Long id);

    boolean updateOrCreateReviewFeedback(Review review, User user, ReviewFeedback feedback);


}
