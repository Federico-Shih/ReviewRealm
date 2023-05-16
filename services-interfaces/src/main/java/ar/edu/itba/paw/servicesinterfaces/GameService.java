package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.dtos.*;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.saving.SubmitGameDTO;
import ar.edu.itba.paw.dtos.searching.GameSearchFilter;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.GameReviewData;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GameService {

    Optional<Game> createGame(SubmitGameDTO gameDTO, long userId);

    Optional<Game> getGameById(Long id);

    List<Genre> getGameGenresById(Long id);

    Paginated<Game> searchGames(Page page, GameSearchFilter filter, Ordering<GameOrderCriteria> ordering);


    GameReviewData getReviewsByGameId(Long id, User activeUser);

    Double getAverageGameReviewRatingById(Long id);

    List<Game> getFavoriteGamesFromUser(long userId);

    List<Game> getPossibleFavGamesFromUser(long userId);

    void deleteFavoriteGame(long userId, long gameId);

    void setFavoriteGames(long userId, List<Long> gameIds);

    void addNewReviewToGame(Long gameId,Integer rating);

    void deleteReviewFromGame(Long gameId,Integer rating);

    void updateReviewFromGame(Long gameId,Integer oldRating,Integer newRating);

    void acceptGame(long gameId);

    void rejectGame(long gameId);

    List<Game> getRecommendationsOfGamesForUser(User user);

    Set<Game> getGamesReviewedByUser(Long userId);
}