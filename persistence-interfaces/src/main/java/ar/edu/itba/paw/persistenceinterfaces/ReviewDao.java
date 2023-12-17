package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.SaveReviewDTO;
import ar.edu.itba.paw.dtos.filtering.ReviewFilter;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.FeedbackType;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface ReviewDao {
    Review create(String title,
                         String content,
                         int rating,
                         Game reviewedGame,
                         User author,
                         Difficulty difficulty,
                         Double gameLength,
                         Platform platform,
                         Boolean completed,
                         Boolean replayable);

    Optional<Review> update(long id, SaveReviewDTO reviewDTO);

    Optional<Review> findById(long id, Long activeUserId);

    Paginated<Review> findAll(Page pagination, ReviewFilter filter, Ordering<ReviewOrderCriteria> ordering, Long activeUserId);

    List<Review> findAll(ReviewFilter filter, Ordering<ReviewOrderCriteria> ordering, Long activeUserId);

    boolean deleteReview(long id);

    int deleteReviewsOfGame(long gameId);

    Optional<ReviewFeedback> editReviewFeedback(long reviewId, long userId, FeedbackType oldFeedback, FeedbackType feedback);

    Optional<ReviewFeedback> addReviewFeedback(long reviewId, long userId, FeedbackType feedback);

    boolean deleteReviewFeedback(long reviewId, long userId, FeedbackType oldfeedback);

    Optional<FeedbackType> getReviewFeedback(long reviewId, long userId);

}
