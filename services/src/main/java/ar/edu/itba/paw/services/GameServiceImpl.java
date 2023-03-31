package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Category;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class GameServiceImpl implements GameService {

    private final GameDao gameDao;

    @Autowired
    public GameServiceImpl(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    @Override
    public Game createGame(String name, String developer, String publisher, String imageUrl, List<Integer> categories, LocalDate publishedDate) {
        List<Category> categoryList = new ArrayList<>();
        for (Integer c : categories) {
            //categoryList.add(categoryDao.getById(c)) TODO categoryDao
        }
        return gameDao.create(name,developer,publisher,imageUrl,categoryList, publishedDate);
    }

    @Override
    public Game getGameById(Integer id) {
        return gameDao.getById(id);
    }

    @Override
    public List<Game> getAllGames() {
        return gameDao.getAll();
    }
}
