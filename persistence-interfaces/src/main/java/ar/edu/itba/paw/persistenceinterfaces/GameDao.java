package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.dtos.Filter;
import ar.edu.itba.paw.exceptions.ObjectNotFoundException;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GameDao {

    Game create(String name,String description,String developer, String publisher, String imageid, List<Genre> genres, LocalDate publishDate);

    Optional<Game> getById(Long id) throws ObjectNotFoundException;

    Paginated<Game> getAll(int page, Integer pageSize, Filter filter, String searchQuery);

    Paginated<Game> getAllShort(int page, Integer pageSize, String searchQuery); //Este no hace las querys para sacar los average score

    List<Review> getReviewsById(Long id);

    List<Genre> getGenresByGame(Long id);

    List<Game> getFavoriteGamesFromUser(long userId);

    List<Game> getRecommendationsForUser(Long userId, List<Integer> userPreferences,  Integer amount);

    Double getAverageReviewRatingById(Long id);

    void addNewReview(Long gameId,Integer rating);

    void modifyReview(Long gameId,Integer oldRating, Integer newRating);

    void deleteReview(Long gameId,Integer rating);

}
