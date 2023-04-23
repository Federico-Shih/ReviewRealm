package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.dtos.ReviewFilter;
import ar.edu.itba.paw.enums.Difficulty;
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
    Optional<Review> getById(Long id);

    Paginated<Review> getAll(ReviewFilter filter, Integer page, Integer pageSize);

    List<Review> getUserReviews(long userId);
}
