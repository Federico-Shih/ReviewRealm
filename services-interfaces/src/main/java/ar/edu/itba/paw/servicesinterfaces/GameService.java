package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.models.Game;

import java.time.LocalDate;
import java.util.List;

public interface GameService {
    Game createGame(String name, String developer, String publisher, String imageUrl, List<Integer> genres,
                    LocalDate publishedDate);
    Game getGameById(Integer id);
    List<Game> getAllGames();
}
