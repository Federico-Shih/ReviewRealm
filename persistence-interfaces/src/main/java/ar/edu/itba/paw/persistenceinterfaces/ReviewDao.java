package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.dtos.ReviewFilter;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;

import java.util.List;

public interface ReviewDao {
    Review create(String title, String content, Integer rating, Game reviewedGame, User author);
    Review getById(Integer id);
    List<Review> getAll(ReviewFilter filter);
}
