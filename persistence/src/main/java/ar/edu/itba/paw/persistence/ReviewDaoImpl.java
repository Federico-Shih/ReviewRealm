package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.dtos.ReviewFilter;
import ar.edu.itba.paw.models.Genre;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
@Repository
public class ReviewDaoImpl implements ReviewDao {

    @Override
    public Review create(String title, String content, Integer rating, Game reviewedGame, User author) {
        return new Review(1234, author, title, content, LocalDate.now(), rating, reviewedGame);
    }

    @Override
    public Review getById(Integer id) {
        return new Review(123, new User(123, "PerottiGamer", "perotti@itba.edu.ar", "12345678", Arrays.asList(new Genre(2,"RTS"), new Genre(3,"Aventura"))), "Completamente Injugable", "El peor juego que jugué en mi vida. Sinceramente, es una pérdida de tiempo y una falta de respeto hacia la industria internacional de videojuegos. Aparte la escena del final me dio miedito. Se supone que es un juego para nenes.", LocalDate.now(), 1, new Game(1984,"Jugando con Hugo", "Juego para toda la familia del conocido Hugo", "Juan Domingo Peron","La Campora","https://th.bing.com/th/id/R.f3bf42bb11207b145468dfe80c65cbc6?rik=4VSBbo0k4fimEQ&pid=ImgRaw&r=0",
                Arrays.asList(new Genre(1,"Terror Psicológico"), new Genre(2,"RTS"), new Genre(3,"First Person Shooter"), new Genre(4,"Anime")),LocalDate.now()));
    }

    @Override
    public List<Review> getAll(ReviewFilter filter) {
        return Arrays.asList(getById(0), getById(0),getById(0), getById(0), getById(0), getById(0));
    }
}
