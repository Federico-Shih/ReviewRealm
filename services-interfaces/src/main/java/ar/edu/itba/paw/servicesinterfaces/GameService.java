package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.exceptions.ObjectNotFoundException;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GameService {
    Game createGame(String name, String description,String developer, String publisher, String imageUrl, List<Integer> genres,
                    LocalDate publishedDate);

    Optional<Game> getGameById(Long id);

    List<Game> getAllGames();

    Optional<List<Review>> getReviewsByGameId(Long id);
}
