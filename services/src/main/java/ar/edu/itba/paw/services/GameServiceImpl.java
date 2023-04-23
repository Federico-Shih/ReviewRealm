package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

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
    public Paginated<GameData> getAllGames(Integer page, Integer pageSize) {
        return gameDao.getAll(page,pageSize);
    }

    @Override
    public Double getAverageGameReviewRatingById(Long id) {
        return gameDao.getAverageReviewRatingById(id);
    }

    @Override
    public GameReviewData getReviewsByGameId(Long id) {
        List<Review> reviews = gameDao.getReviewsById(id);
        if(reviews.size()>0) {
            int sumRating = 0;
            HashMap<Difficulty, Integer> difficultyCount = new HashMap<>();
            HashMap<Platform, Integer> platformCount = new HashMap<>();


            double sumHours = 0;
            int sumReplayability = 0;
            int sumCompletability = 0;
            for (Review r : reviews) {
                sumRating += r.getRating();
                difficultyCount.put(r.getDifficulty(), difficultyCount.getOrDefault(r.getDifficulty(), 0) + 1);
                platformCount.put(r.getPlatform(), platformCount.getOrDefault(r.getPlatform(), 0) + 1);

                sumHours += (r.getGameLength() != null)? r.getGameLength():0;
                sumReplayability += (r.getReplayability()!= null)? ((r.getReplayability())? 1:0):0;
                sumCompletability += (r.getCompleted() != null)? ((r.getCompleted())? 1:0):0;
            }
            difficultyCount.remove(null); //Sacamos a todos los que no tenian difficulty de la ecuacion
            Difficulty averageDiff = difficultyCount.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();
            platformCount.remove(null);
            Platform averagePlatform = platformCount.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();
            return new GameReviewData(reviews, (double) sumRating/  reviews.size(),averageDiff,averagePlatform,
                    sumHours / reviews.size(),(double) sumReplayability/ reviews.size(), (double) sumCompletability /reviews.size());
        }
        return new GameReviewData(reviews,-1, null, null,-1,-1,-1);
    }

    @Override
    public List<Game> getFavoriteGamesFromUser(long userId) {
        return gameDao.getFavoriteGamesFromUser(userId);
    }
}
