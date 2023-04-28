package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.Filter;
import ar.edu.itba.paw.dtos.SubmitGameDTO;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import ar.edu.itba.paw.servicesinterfaces.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class GameServiceImpl implements GameService {

    private static final String IMAGE_PREFIX = "/images";
    private final GameDao gameDao;
    private final GenreService genreServ;
    private final ImageService imgService;

    @Autowired
    public GameServiceImpl(GameDao gameDao, GenreService genreServ, ImageService imgService) {
        this.gameDao = gameDao;
        this.genreServ = genreServ;
        this.imgService = imgService;
    }

    @Override
    public Game createGame(String name,String description ,String developer, String publisher, String imageUrl, List<Integer> genres, LocalDate publishedDate) {
        List<Genre> genreList = new ArrayList<>();
        Optional<Genre> g;
        for (Integer c : genres) {
             g = genreServ.getGenreById(c);
            g.ifPresent(genreList::add);
        }
        return gameDao.create(name,description,developer,publisher,imageUrl, genreList, publishedDate);
    }

    @Override
    public Game createGame(SubmitGameDTO gameDTO) {
        Image img = imgService.uploadImage(gameDTO.getImageData(), gameDTO.getMediatype());
        if (img == null) {
            // TODO error
            throw new RuntimeException("Unable to create image");
        }
        return createGame(gameDTO.getName(),
                gameDTO.getDescription(),
                gameDTO.getDeveloper(),
                gameDTO.getPublisher(),
                IMAGE_PREFIX + "/" + img.getId(),
                gameDTO.getGenres(),
                LocalDate.now());
    }

    @Override
    public Optional<Game> getGameById(Long id) {
        return gameDao.getById(id);
    }

    @Override
    public  Paginated<GameData> getAllGames(Integer page, Integer pageSize, Filter filter,String searchQuery)
    {
        return gameDao.getAll(page,pageSize,filter,searchQuery);
    }

    @Override
    public Paginated<Game> getAllGamesShort(Integer page, Integer pageSize, String searchQuery) {
        return gameDao.getAllShort(page,pageSize,searchQuery);
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
            Optional<Map.Entry<Difficulty, Integer>> averageDiff = difficultyCount.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue));
            platformCount.remove(null);
            Optional<Map.Entry<Platform, Integer>> averagePlatform = platformCount.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue));
            return new GameReviewData(reviews, (double) sumRating/  reviews.size(),(averageDiff.isPresent())? averageDiff.get().getKey() : null,(averagePlatform.isPresent())? averagePlatform.get().getKey() : null,
                    sumHours / reviews.size(),(double) sumReplayability/ reviews.size(), (double) sumCompletability /reviews.size());
        }
        return new GameReviewData(reviews,-1, null, null,-1,-1,-1);
    }

    @Override
    public List<Game> getFavoriteGamesFromUser(long userId) {
        return gameDao.getFavoriteGamesFromUser(userId);
    }
}
