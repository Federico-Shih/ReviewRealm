package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.*;
import ar.edu.itba.paw.dtos.filtering.GameFilter;
import ar.edu.itba.paw.dtos.filtering.GameFilterBuilder;
import ar.edu.itba.paw.dtos.filtering.ReviewFilter;
import ar.edu.itba.paw.dtos.filtering.ReviewFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
import ar.edu.itba.paw.dtos.saving.SubmitGameDTO;
import ar.edu.itba.paw.dtos.searching.GameSearchFilter;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.exceptions.NoSuchGameException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
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

    @Transactional
    @Override
    public Optional<Game> createGame(SubmitGameDTO gameDTO, long userId) {
        Image img = imgService.uploadImage(gameDTO.getImageData(), gameDTO.getMediatype());
        if (img == null) {
            throw new RuntimeException("Error creating image");
        }

        boolean isModerator = userService
                .getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("user.notfound"))
                .getRoles()
                .stream().anyMatch(role -> role.getRoleName().equals("MODERATOR"));

        LOGGER.info("{} game - name: {}, developer: {}", isModerator? "Creating":"Suggesting",gameDTO.getName(), gameDTO.getName());
        List<Genre> genreList = new ArrayList<>();
        Optional<Genre> g;
        for (Integer c : gameDTO.getGenres()) {
            g = genreServ.getGenreById(c);
            g.ifPresent(genreList::add);
        }
        // TODO: Habría que preguntar si acá hay que armar el optional o si está bien como está hecho en el Dao, porque creo los Dao deberían devolver Optional
        return gameDao.create(gameDTO.getName(), gameDTO.getDescription(), gameDTO.getDeveloper(), gameDTO.getPublisher(), img.getId(), genreList, LocalDate.now(), !isModerator);
    }
    @Transactional(readOnly = true)
    @Override
    public Optional<Game> getGameById(Long id) {
        return gameDao.getById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Genre> getGameGenresById(Long id) {
        return gameDao.getGenresByGame(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<Game> searchGames(Page page, GameSearchFilter searchFilter, Ordering<GameOrderCriteria> ordering)
    {
        GameFilter filter = new GameFilterBuilder()
                .withGameContent(searchFilter.getSearch())
                .withGameGenres(searchFilter.getGenres())
                .withRatingRange(searchFilter.getMinRating(), searchFilter.getMaxRating())
                .withSuggestion(searchFilter.getSuggestion())
                .build();
        return gameDao.findAll(page, filter, ordering);
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<Game> getAllGamesShort(Integer page, Integer pageSize, String searchQuery) {
        GameFilterBuilder gameFilterBuilder = new GameFilterBuilder().withGameContent(searchQuery);
        return gameDao.findAll(Page.with(page, pageSize), gameFilterBuilder.build(), new Ordering<>(OrderDirection.ASCENDING, GameOrderCriteria.NAME));
    }

    @Transactional(readOnly = true)
    @Override
    public Double getAverageGameReviewRatingById(Long id) {
        return gameDao.getAverageReviewRatingById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public GameReviewData getReviewsByGameId(Long id, User activeUser) {
        // TODO: PAGINAR
        ReviewFilter filter = new ReviewFilterBuilder().withGameId(id.intValue()).build();
        List<Review> reviews = reviewDao.findAll(Page.with(1, 1000), filter, new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_DATE),activeUser.getId()).getList();
        if(reviews.size() > 0) {
            HashMap<Difficulty, Integer> difficultyCount = new HashMap<>();
            HashMap<Platform, Integer> platformCount = new HashMap<>();

            double sumHours = 0;
            int sumReplayability = 0;
            int sumCompletability = 0;
            for (Review r : reviews) {
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
            return new GameReviewData(reviews, reviews.get(0).getReviewedGame().getAverageRating(),(averageDiff.isPresent())? averageDiff.get().getKey() : null,(averagePlatform.isPresent())? averagePlatform.get().getKey() : null,
                    sumHours / reviews.size(), ((double)  sumReplayability/ reviews.size() )* 100,  ((double)sumCompletability /reviews.size())*100);
        }
        return new GameReviewData(reviews,-1, null, null,-1,-1,-1);
    }

    @Transactional
    @Override
    public void addNewReviewToGame(Long gameId, Integer rating) {
        gameDao.addNewReview(gameId, rating);
    }

    @Transactional
    @Override
    public void deleteReviewFromGame(Long gameId, Integer reviewRating) {
        gameDao.deleteReview(gameId,reviewRating);
    }

    @Transactional
    @Override
    public void updateReviewFromGame(Long gameId, Integer oldRating, Integer newRating) {
        gameDao.modifyReview(gameId, oldRating, newRating);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Game> getRecommendationsOfGamesForUser(Long userId) {
        Optional<User> user = userService.getUserById(userId);
        if(!user.isPresent() || !user.get().hasPreferencesSet()){
            return new ArrayList<>();
        }
        Set <Genre> userPreferences = user.get().getPreferences();
        List<Integer> preferencesIds = userPreferences.stream().map(Genre::getId).collect(Collectors.toList());
        Set<Game> userReviewedGames = getGamesReviewedByUser(userId);
        List<Long> idsToExclude = userReviewedGames.stream().map(Game::getId).collect(Collectors.toList());

        return gameDao.getRecommendationsForUser(userId,preferencesIds,idsToExclude);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Game> getGamesReviewedByUser(Long userId) {
        //TODO: revisar uso de DAOs llamadas circulares entre services
        List<Long> authors = new ArrayList<>();
        authors.add(userId);
        List<Review> userReviews = reviewDao.findAll(Page.with(1, 100), new ReviewFilterBuilder().withAuthors(authors).build(), Ordering.defaultOrder(ReviewOrderCriteria.REVIEW_DATE),  null ).getList();
        Set<Game> reviewedGames = new HashSet<>();
        userReviews.forEach( review -> reviewedGames.add(review.getReviewedGame()));
        return reviewedGames;
    }

    @Transactional
    @Override
    public void acceptGame(long gameId) {
        decisionGame(gameDao::setSuggestedFalse, gameId);
    }

    @Transactional
    @Override
    public void rejectGame(long gameId) {
        decisionGame(gameDao::deleteGame, gameId);
    }

    private void decisionGame(Function<Long, Boolean> decisionFunction, Long gameId) {
        if(!decisionFunction.apply(gameId))
            throw new NoSuchGameException(String.format("There's no game with such id: %d", gameId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Game> getFavoriteGamesFromUser(long userId) {
        return gameDao.getFavoriteGamesFromUser(userId);
    }
}
