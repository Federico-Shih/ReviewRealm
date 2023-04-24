package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.dtos.Filter;
import ar.edu.itba.paw.exceptions.ObjectNotFoundException;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.GameData;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GameDao {

    Game create(String name,String description,String developer, String publisher, String imageUrl, List<Genre> genres, LocalDate publishDate);

    Optional<Game> getById(Long id) throws ObjectNotFoundException;

    Paginated<GameData> getAll(int page, Integer pageSize, Filter filter, String searchQuery);

    List<Review> getReviewsById(Long id);

    List<Genre> getGenresByGame(Long id);


    List<Game> getFavoriteGamesFromUser(long userId);

    Double getAverageReviewRatingById(Long id);
}
