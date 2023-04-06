package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Genre;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
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
    public Game create(String name,String description,String developer, String publisher, String imageUrl, List<Genre> genres, LocalDate publishDate) {
        return new Game(1984,description,name, developer, publisher, imageUrl, genres, publishDate);
    }

    @Override
    public Game getById(Integer id) {
        return new Game(1984,"Jugando con Hugo", "El famoso juego de jugando con Hugo amado por todas las familias argentina, tambien tiene la posibildad de Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo " +
                "dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.","Juan Domingo Peron","La Campora","https://th.bing.com/th/id/R.f3bf42bb11207b145468dfe80c65cbc6?rik=4VSBbo0k4fimEQ&pid=ImgRaw&r=0",
                Arrays.asList(new Genre(1,"FPS"),new Genre(2,"RogueLike")),LocalDate.now());
    }

    @Override
    public List<Review> getReviewsById(Integer id) {
        return Arrays.asList(new Review( id, new User(89,"Perotti@email.com","123213123"), "No me gusto", "Muy bueno", LocalDate.now(),9,getById(id)),
                new Review( id, new User(89,"Perotti@email.com","123213123"), "No me gusto", "Muy bueno", LocalDate.now(),9,getById(id)));
    }
}
