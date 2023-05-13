package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.dtos.*;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.GameReviewData;
import ar.edu.itba.paw.models.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GameService {

    Optional<Game> createGame(SubmitGameDTO gameDTO, long userId);

    Optional<Game> getGameById(Long id);

    List<Genre> getGameGenresById(Long id);

    Paginated<Game> getAllGames(Page page, GameFilter filter, Ordering<GameOrderCriteria> ordering);

    Paginated<Game> getAllGamesShort(Integer page, Integer pageSize,String searchQuery);

    GameReviewData getReviewsByGameId(Long id, User activeUser);

    Double getAverageGameReviewRatingById(Long id);

    List<Game> getFavoriteGamesFromUser(long userId);

    void addNewReviewToGame(Long gameId,Integer rating);

    void deleteReviewFromGame(Long gameId,Integer rating);

    void updateReviewFromGame(Long gameId,Integer oldRating,Integer newRating);

    List<Game> getRecommendationsOfGamesForUser(Long userId, Integer min, Integer max);
}