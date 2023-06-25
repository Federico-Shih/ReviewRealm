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
    //ok moderator, ok not moderator, exc
    Optional<Game> createGame(SubmitGameDTO gameDTO, long userId);

    boolean deleteGame(long gameId);
    //ok
    Game editGame(SubmitGameDTO gameDTO, long userId);

    Optional<Game> getGameById(long id);
    //normal search, empty search
    Paginated<Game> searchGames(Page page, GameFilter filter, Ordering<GameOrderCriteria> ordering);
    //normal
    Paginated<Game> searchGamesNotReviewedByUser(Page page, String search, Ordering<GameOrderCriteria> ordering, long userId);
    //exc, ok game with reviews, ok game without reviews
    GameReviewData getGameReviewDataByGameId(long id);

    Game addNewReviewToGame(long gameId, int rating);

    Game deleteReviewFromGame(long gameId, int rating);

    Game updateReviewFromGame(long gameId, int oldRating, int newRating);
    //ok
    Game acceptGame(long gameId, long approvingUserId);
    //ok with existing game, ok with inexistent game
    boolean rejectGame(long gameId, long rejectingUserId);
    //exc, normal
    List<Game> getRecommendationsOfGamesForUser(long userId);

    Set<Game> getGamesReviewedByUser(long userId);
}