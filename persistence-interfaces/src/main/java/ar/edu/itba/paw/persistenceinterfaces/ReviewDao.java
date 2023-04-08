package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.dtos.ReviewFilter;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface ReviewDao {
    Review create(String title, String content, Integer rating, Game reviewedGame, User author);
    Optional<Review> getById(Long id);
    List<Review> getAll(ReviewFilter filter);
}
