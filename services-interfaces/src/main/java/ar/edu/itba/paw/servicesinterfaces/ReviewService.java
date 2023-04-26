package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.dtos.Filter;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;

import javax.naming.AuthenticationException;
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
    Optional<Review> getReviewById(Long id);

    List<Review> getUserReviews(long userId);

    Paginated<Review> getAllReviews(Filter filter, Integer page, Integer pageSize);

    boolean deleteReviewById(Long id);
}
