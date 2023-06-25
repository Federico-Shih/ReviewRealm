package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.dtos.*;
import ar.edu.itba.paw.dtos.filtering.GameFilter;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.saving.SubmitGameDTO;
import ar.edu.itba.paw.models.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GameService {
    //
    Optional<Game> createGame(SubmitGameDTO gameDTO, long userId);
    //
    boolean deleteGame(long gameId);
    //
    Game editGame(SubmitGameDTO gameDTO, long userId);
    //
    Optional<Game> getGameById(long id);
    //
    Paginated<Game> searchGames(Page page, GameFilter filter, Ordering<GameOrderCriteria> ordering);
    //
    Paginated<Game> searchGamesNotReviewedByUser(Page page, String search, Ordering<GameOrderCriteria> ordering, long userId);
    //
    GameReviewData getGameReviewDataByGameId(long id);
    //
    Game addNewReviewToGame(long gameId, int rating);
    //
    Game deleteReviewFromGame(long gameId, int rating);
    //
    Game updateReviewFromGame(long gameId, int oldRating, int newRating);
    //
    Game acceptGame(long gameId, long approvingUserId);
    //
    boolean rejectGame(long gameId, long rejectingUserId);
    //
    List<Game> getRecommendationsOfGamesForUser(long userId);
    //
    Set<Game> getGamesReviewedByUser(long userId);
}