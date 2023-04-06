package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.exceptions.ObjectNotFoundException;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;

import java.time.LocalDate;
import java.util.List;

public interface GameService {
    Game createGame(String name, String description,String developer, String publisher, String imageUrl, List<Integer> genres,
                    LocalDate publishedDate);

    Game getGameById(Integer id);
    List<Game> getAllGames();

    List<Review> getReviewsByGameId(Integer id);
}
