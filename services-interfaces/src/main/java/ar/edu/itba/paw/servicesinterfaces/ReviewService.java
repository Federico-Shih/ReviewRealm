package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.ReviewFilter;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.FeedbackType;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.exceptions.ReviewAlreadyExistsException;
import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review createReview(String title,
                        String content,
                        int rating,
                        long authorId,
                        long gameId,
                        Difficulty difficulty,
                        Double gameLength,
                        Platform platform,
                        Boolean completed,
                        Boolean replayable) throws ReviewAlreadyExistsException;

    Review updateReview(long id,
                     String title,
                     String content,
                     int rating,
                     Difficulty difficulty,
                     Double gameLength,
                     Platform platform,
                     Boolean completed,
                     Boolean replayable);

    Optional<Review> getReviewById(long id, Long activeUserId);

    Paginated<Review> getUserReviews(Page page, long userId, Long activeUserId);

    Paginated<Review> searchReviews(Page page, ReviewFilter filter, Ordering<ReviewOrderCriteria> ordering, Long activeUserId);

    Paginated<Review> getReviewsFromGame(Page page, long gameId, Long activeUserId, boolean excludeActiveUser);

    List<Review> getAllReviewsFromGame(long gameId, Long activeUserId);

    Paginated<Review> getReviewsFromFollowingByUser(long userId, Page page);

    Paginated<Review> getRecommendedReviewsByUser(long userId, Page page);

    Paginated<Review> getNewReviewsExcludingActiveUser(Page page, Long activeUserId);

    boolean deleteReviewById(long id);

    ReviewFeedback updateOrCreateReviewFeedback(long reviewId, long userId, FeedbackType feedback);

    Optional<Review> getReviewOfUserForGame(long userId, long gameId);
}
