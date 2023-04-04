package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Genre;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Repository
public class GameDaoImpl implements GameDao {

    @Override
    public List<Game> getAll() {
        return Arrays.asList(getById(0), getById(0));
    }

    @Override
    public Game create(String name, String developer, String publisher, String imageUrl, List<Genre> genres, LocalDate publishDate) {
        return new Game(1984, name, developer, publisher, imageUrl, genres, publishDate);
    }

    @Override
    public Game getById(Integer id) {
        return new Game(1984,"Jugando con Hugo", "Juan Domingo Peron","La Campora","https://th.bing.com/th/id/R.f3bf42bb11207b145468dfe80c65cbc6?rik=4VSBbo0k4fimEQ&pid=ImgRaw&r=0",
                Arrays.asList(new Genre(1,"FPS"),new Genre(2,"RogueLike")),LocalDate.now());
    }

}
