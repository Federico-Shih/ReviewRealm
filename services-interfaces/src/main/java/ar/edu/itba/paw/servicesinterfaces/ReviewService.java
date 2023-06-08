package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
import ar.edu.itba.paw.dtos.searching.ReviewSearchFilter;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.FeedbackType;
import ar.edu.itba.paw.enums.Platform;
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

    Paginated<Review> getUserReviews(Page page, Long userId,User activeUser);

    Paginated<Review> searchReviews(Page page, ReviewSearchFilter filter, Ordering<ReviewOrderCriteria> ordering, User activeUser);

    Paginated<Review> getReviewsFromGame(Page page, Long gameId, User activeUser);

    List<Review> getAllReviewsFromGame(Long gameId, User activeUser);

    Paginated<Review> getReviewsFromFollowingByUser(Long userId, Page page);

    Paginated<Review> getRecommendedReviewsByUser(User user, Page page);

    Paginated<Review> getNewReviewsExcludingActiveUser(Page page, User activeUser);


    boolean deleteReviewById(Long id);

    boolean updateOrCreateReviewFeedback(Review review, User user, FeedbackType feedback);
}
