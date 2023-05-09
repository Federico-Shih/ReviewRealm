package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.*;
import ar.edu.itba.paw.dtos.builders.GameFilterBuilder;
import ar.edu.itba.paw.dtos.builders.ReviewFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import ar.edu.itba.paw.servicesinterfaces.ImageService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameServiceImpl.class);

    private static final String IMAGE_PREFIX = "/images";
    private final GameDao gameDao;
    private final ReviewDao reviewDao;
    private final GenreService genreServ;
    private final ImageService imgService;

    private final UserService userService;

    @Autowired
    public GameServiceImpl(GameDao gameDao, ReviewDao reviewDao, GenreService genreServ, ImageService imgService, UserService userService) {
        this.gameDao = gameDao;
        this.reviewDao = reviewDao;
        this.genreServ = genreServ;
        this.imgService = imgService;

        this.userService = userService;
    }

    @Override
    public Game createGame(String name, String description , String developer, String publisher, String imageid, List<Integer> genres, LocalDate publishedDate) {
        LOGGER.info("Creating game - name: {}, developer: {}", name, publisher);
        List<Genre> genreList = new ArrayList<>();
        Optional<Genre> g;
        for (Integer c : genres) {
             g = genreServ.getGenreById(c);
            g.ifPresent(genreList::add);
        }
        return gameDao.create(name,description,developer,publisher, imageid, genreList, publishedDate);
    }

    @Override
    public Game createGame(SubmitGameDTO gameDTO) {
        Image img = imgService.uploadImage(gameDTO.getImageData(), gameDTO.getMediatype());
        if (img == null) {
            throw new RuntimeException("Error creating image");
        }
        return createGame(gameDTO.getName(),
                gameDTO.getDescription(),
                gameDTO.getDeveloper(),
                gameDTO.getPublisher(),
                img.getId(),
                gameDTO.getGenres(),
                LocalDate.now());
    }

    @Override
    public Optional<Game> getGameById(Long id) {
        return gameDao.getById(id);
    }

    @Override
    public Paginated<Game> getAllGames(Page page, GameFilter filter, Ordering<GameOrderCriteria> ordering)
    {
        return gameDao.findAll(page, filter, ordering);
    }

    @Override
    public Paginated<Game> getAllGamesShort(Integer page, Integer pageSize, String searchQuery) {
        GameFilterBuilder gameFilterBuilder = new GameFilterBuilder().withGameContent(searchQuery);
        return gameDao.findAll(Page.with(page, pageSize), gameFilterBuilder.getFilter(), new Ordering<>(OrderDirection.ASCENDING, GameOrderCriteria.NAME));
    }

    @Override
    public Double getAverageGameReviewRatingById(Long id) {
        return gameDao.getAverageReviewRatingById(id);
    }

    @Override
    public GameReviewData getReviewsByGameId(Long id, User activeUser) {
        // TODO: PAGINAR
        ReviewFilter filter = new ReviewFilterBuilder().withGameId(id.intValue()).getFilter();
        List<Review> reviews = reviewDao.findAll(Page.with(1, 1000), filter, new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_DATE),(activeUser !=null)? activeUser.getId() : null).getList();
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
                    sumHours / reviews.size(),(double) (sumReplayability/ reviews.size() )* 100, (double) (sumCompletability /reviews.size())*100);
        }
        return new GameReviewData(reviews,-1, null, null,-1,-1,-1);
    }

    @Override
    public void addNewReviewToGame(Long gameId, Integer rating) {
        gameDao.addNewReview(gameId, rating);
    }

    @Override
    public void deleteReviewFromGame(Long gameId, Integer reviewRating) {
        gameDao.deleteReview(gameId,reviewRating);
    }

    @Override
    public void updateReviewFromGame(Long gameId, Integer oldRating, Integer newRating) {
        gameDao.modifyReview(gameId, oldRating, newRating);
    }


    @Override
    public List<Game> getRecommendationsOfGamesForUser(Long userId, Integer min, Integer max) {
        List <Genre> userPreferences = userService.getPreferences(userId);
        if(userPreferences.isEmpty()){
            return new ArrayList<>();
        }
        List<Game> games = gameDao.getRecommendationsForUser(userId,userPreferences.stream().map(Genre::getId).collect(Collectors.toList()), max);
        if(games.size() < min) {
            return new ArrayList<>();
        }
        return games;
    }

    @Override
    public List<Game> getFavoriteGamesFromUser(long userId) {
        return gameDao.getFavoriteGamesFromUser(userId);
    }
}
