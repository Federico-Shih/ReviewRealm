package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.dtos.*;
import ar.edu.itba.paw.dtos.filtering.GameFilter;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GameDao {

    Optional<Game> create(String name,String description,String developer, String publisher, String imageid, List<Genre> genres, LocalDate publishDate, boolean suggested);

    Optional<Game> getById(Long id);

    Paginated<Game> findAll(Page page, GameFilter filter, Ordering<GameOrderCriteria> ordering);

    List<Genre> getGenresByGame(Long id);

    List<Game> getFavoriteGamesFromUser(long userId);

    List<Game> getFavoriteGamesCandidates(long userId, int minRating);

    void deleteFavoriteGameForUser(long userId, long gameId);

    List<Game> getRecommendationsForUser(Long userId, List<Integer> userPreferences,List<Long> gamesToExclude);

    Set<Game> getGamesReviewdByUser(Long userId);

    Double getAverageReviewRatingById(Long id);

    void addNewReview(Long gameId,Integer rating);

    void modifyReview(Long gameId,Integer oldRating, Integer newRating);

    void deleteReview(Long gameId,Integer rating);

    boolean setSuggestedFalse(long gameId);

    boolean deleteGame(long gameId);

    void replaceAllFavoriteGames(long userId, List<Long> gameIds);
}
