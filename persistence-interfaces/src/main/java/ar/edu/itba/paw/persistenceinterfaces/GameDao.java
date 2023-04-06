package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.exceptions.ObjectNotFoundException;
import ar.edu.itba.paw.models.Genre;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;

import java.time.LocalDate;
import java.util.List;

public interface GameDao {

    Game create(String name,String description,String developer, String publisher, String imageUrl, List<Genre> genres, LocalDate publishDate);

    Game getById(Integer id) throws ObjectNotFoundException;

    List<Game> getAll();

    List<Review> getReviewsById(Integer id);
}
