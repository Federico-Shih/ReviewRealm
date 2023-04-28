package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.dtos.Filter;
import ar.edu.itba.paw.dtos.SubmitGameDTO;
import ar.edu.itba.paw.exceptions.ObjectNotFoundException;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.GameReviewData;
import ar.edu.itba.paw.models.GameData;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GameService {
    Game createGame(String name, String description,String developer, String publisher, String imageUrl, List<Integer> genres,
                    LocalDate publishedDate);

    Game createGame(SubmitGameDTO gameDTO);

    Optional<Game> getGameById(Long id);

    Paginated<GameData> getAllGames(Integer page, Integer pageSize, Filter filter,String searchQuery);

    Paginated<Game> getAllGamesShort(Integer page, Integer pageSize,String searchQuery);

    GameReviewData getReviewsByGameId(Long id);

    Double getAverageGameReviewRatingById(Long id);

    List<Game> getFavoriteGamesFromUser(long userId);
}