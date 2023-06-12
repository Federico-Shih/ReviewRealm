package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.GameFilter;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GameDao {

    Game create(String name,String description,String developer, String publisher, String imageid, List<Genre> genres, LocalDate publishDate, boolean suggested);

    Optional<Game> edit(long gameId, String name, String description, String developer, String publisher, String imageid, List<Genre> genres);

    Optional<Game> getById(long id);

    Paginated<Game> findAll(Page page, GameFilter filter, Ordering<GameOrderCriteria> ordering);

    List<Genre> getGenresByGame(long id);

    Optional<List<Game>> getFavoriteGamesFromUser(long userId);

    List<Game> getFavoriteGamesCandidates(long userId, int minRating);

    boolean deleteFavoriteGameForUser(long userId, long gameId);

    List<Game> getRecommendationsForUser(List<Integer> userPreferences,List<Long> gamesToExclude);

    Optional<Set<Game>> getGamesReviewedByUser(long userId);

    Optional<Game> addNewReview(long gameId, int rating);

    Optional<Game> modifyReview(long gameId, int oldRating, int newRating);

    Optional<Game> deleteReviewFromGame(long gameId, int rating);

    Optional<Game> setSuggestedFalse(long gameId);

    boolean deleteGame(long gameId);

    Optional<User> replaceAllFavoriteGames(long userId, List<Long> gameIds);
}
