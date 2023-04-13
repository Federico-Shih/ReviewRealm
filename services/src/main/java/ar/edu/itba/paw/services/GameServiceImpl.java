package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.ObjectNotFoundException;
import ar.edu.itba.paw.models.Genre;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GameServiceImpl implements GameService {

    private final GameDao gameDao;
    private final GenreService genreServ;
    @Autowired
    public GameServiceImpl(GameDao gameDao,GenreService genreServ) {
        this.gameDao = gameDao;
        this.genreServ = genreServ;
    }

    @Override
    public Game createGame(String name,String description ,String developer, String publisher, String imageUrl, List<Integer> genres, LocalDate publishedDate) {
        List<Genre> genreList = new ArrayList<>();
        Optional<Genre> g;
        for (Integer c : genres) {
             g = genreServ.getGenreById(c);
             if(g.isPresent()){
                 genreList.add(g.get());
             }else{
                 //TODO ver que hacemos aca
             }
        }
        return gameDao.create(name,description,developer,publisher,imageUrl, genreList, publishedDate);
    }

    @Override
    public Optional<Game> getGameById(Long id) {
        return gameDao.getById(id);
    }

    @Override
    public List<Game> getAllGames() {
        return gameDao.getAll();
    }

    @Override
    public List<Review> getReviewsByGameId(Long id) {
        return gameDao.getReviewsById(id);
    }
}
