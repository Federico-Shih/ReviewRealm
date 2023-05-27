package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.SaveReviewDTO;
import ar.edu.itba.paw.dtos.filtering.ReviewFilter;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.FeedbackType;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface ReviewDao {
    Review create(String title,
                         String content,
                         Integer rating,
                         Game reviewedGame,
                         User author,
                         Difficulty difficulty,
                         Double gameLength,
                         Platform platform,
                         Boolean completed,
                         Boolean replayable);

    int update(Long id, SaveReviewDTO reviewDTO);

    Optional<Review> findById(Long id, Long activeUserId);

    Paginated<Review> findAll(Page pagination, ReviewFilter filter, Ordering<ReviewOrderCriteria> ordering, Long activeUserId);

    List<Review> findAll(ReviewFilter filter, Ordering<ReviewOrderCriteria> ordering, Long activeUserId);

    boolean deleteReview(Long id);

    boolean editReviewFeedback(Long reviewId, Long userId, FeedbackType oldFeedback, FeedbackType feedback);

    boolean addReviewFeedback(Long reviewId, Long userId, FeedbackType feedback);

    boolean deleteReviewFeedback(Long reviewId, Long userId, FeedbackType oldfeedback);

    FeedbackType getReviewFeedback(Long reviewId, Long userId);

}
