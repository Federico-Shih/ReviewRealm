package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.GameFilter;
import ar.edu.itba.paw.dtos.filtering.GameFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.saving.SubmitGameDTO;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Mission;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.exceptions.GameNotFoundException;
import ar.edu.itba.paw.exceptions.GenreNotFoundException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import ar.edu.itba.paw.servicesinterfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameServiceImpl.class);
    private final GameDao gameDao;
    private final ReviewService reviewService;
    private final ImageService imgService;
    private final UserService userService;
    private final MissionService missionService;
    private final MailingService mailingService;

    @Lazy
    @Autowired
    public GameServiceImpl(GameDao gameDao, ReviewService reviewService, ImageService imgService, UserService userService, MissionService missionService, MailingService mailingService) {
        this.gameDao = gameDao;
        this.reviewService = reviewService;
        this.imgService = imgService;
        this.userService = userService;
        this.missionService = missionService;
        this.mailingService = mailingService;
    }

    @Transactional
    @Override
    public Optional<Game> createGame(SubmitGameDTO gameDTO, long userId) {
        Image img = imgService.uploadImage(gameDTO.getImageData(), gameDTO.getMediatype());
        if (img == null) {
            throw new RuntimeException("Error creating image");
        }

        User user = userService
                .getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("user.notfound"));

        boolean isModerator = user
                .getRoles()
                .stream().anyMatch(role -> role.equals(RoleType.MODERATOR));

        LOGGER.info("{} game - name: {}, developer: {}", isModerator ? "Creating" : "Suggesting", gameDTO.getName(), gameDTO.getName());
        List<Genre> genreList = prepareGenres(gameDTO);

        Game toReturn = gameDao.create(gameDTO.getName(), gameDTO.getDescription(), gameDTO.getDeveloper(), gameDTO.getPublisher(), img.getId(), genreList, gameDTO.getReleaseDate(), !isModerator, user);
        if(!isModerator) {
            mailingService.sendSuggestionInReviewEmail(toReturn, user);
            missionService.addMissionProgress(user.getId(), Mission.RECOMMEND_GAMES, 1f);
        }
        return (isModerator) ? Optional.of(toReturn) : Optional.empty();
    }

    @Transactional
    @Override
    public boolean deleteGame(long gameId) {
        return gameDao.deleteGame(gameId);
    }

    @Transactional
    @Override
    public Game editGame(SubmitGameDTO gameDTO, long gameId) {
        Image img = null;
        if(gameDTO.getImageData().length!=0) {
            img = imgService.uploadImage(gameDTO.getImageData(), gameDTO.getMediatype());
            if (img == null)
                throw new RuntimeException("Error creating image");
        }

        List<Genre> genreList = prepareGenres(gameDTO);
        LOGGER.info("Editing game - name: {}, developer: {}", gameDTO.getName(), gameDTO.getName());
        return gameDao.edit(gameId,gameDTO.getName(),gameDTO.getDescription(),gameDTO.getDeveloper(),gameDTO.getPublisher(), (img!=null)? img.getId() : null, genreList,gameDTO.getReleaseDate()).orElseThrow(GameNotFoundException::new);
    }

    private List<Genre> prepareGenres(SubmitGameDTO gameDTO) throws GenreNotFoundException {
        List<Genre> genreList = new ArrayList<>();
        Optional<Genre> g;
        if (gameDTO.getGenres() != null) {
            for (Integer c : gameDTO.getGenres()) {
                g = Genre.getById(c);
                if(g.isPresent()) {
                    genreList.add(g.get());
                }
                else {
                    throw new GenreNotFoundException();
                }
            }
        }
        return genreList;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Game> getGameById(long id) {
        return gameDao.getById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<Game> searchGames(Page page, GameFilter searchFilter, Ordering<GameOrderCriteria> ordering)
    {
        return gameDao.findAll(page, searchFilter, ordering);
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<Game> searchGamesNotReviewedByUser(Page page, String search, Ordering<GameOrderCriteria> ordering, long userId) {
        List<Long> gamesReviewed = getGamesReviewedByUser(userId).stream().map(Game::getId).collect(Collectors.toList());
        GameFilter gameFilter = new GameFilterBuilder().withGameContent(search).withGamesToExclude(gamesReviewed).build();
        return gameDao.findAll(page, gameFilter, ordering);
    }


    @Transactional(readOnly = true)
    @Override
    public GameReviewData getGameReviewDataByGameId(long id) {
        if(!getGameById(id).isPresent())
            throw new GameNotFoundException();
        List<Review> reviews = reviewService.getAllReviewsFromGame(id,null);
        if(reviews.size() > 0) {
            HashMap<Difficulty, Integer> difficultyCount = new HashMap<>();
            HashMap<Platform, Integer> platformCount = new HashMap<>();

            double sumHours = 0;
            int sumReplayability = 0;
            int sumCompletability = 0;
            for (Review r : reviews) {
                difficultyCount.put(r.getDifficulty(), difficultyCount.getOrDefault(r.getDifficulty(), 0) + 1);
                platformCount.put(r.getPlatform(), platformCount.getOrDefault(r.getPlatform(), 0) + 1);

                sumHours += (r.getGameLength() != null) ? r.getGameLength() : 0;
                sumReplayability += (r.getReplayability() != null) ? ((r.getReplayability()) ? 1 : 0) : 0;
                sumCompletability += (r.getCompleted() != null) ? ((r.getCompleted()) ? 1 : 0) : 0;
            }
            difficultyCount.remove(null); //We remove all the ones that didn't have difficulty set
            Optional<Map.Entry<Difficulty, Integer>> averageDiff = difficultyCount.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue));
            platformCount.remove(null);
            Optional<Map.Entry<Platform, Integer>> averagePlatform = platformCount.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue));
            return new GameReviewData(reviews.get(0).getReviewedGame().getAverageRating(), averageDiff.map(Map.Entry::getKey).orElse(null), averagePlatform.map(Map.Entry::getKey).orElse(null),
                    sumHours / reviews.size(), ((double) sumReplayability / reviews.size()) * 100, ((double) sumCompletability / reviews.size()) * 100);
        }
        return new GameReviewData(-1, null, null,-1,-1,-1);
    }

    @Transactional
    @Override
    public Game addNewReviewToGame(long gameId, int rating) {
        return gameDao.addNewReview(gameId, rating).orElseThrow(GameNotFoundException::new);
    }

    @Transactional
    @Override
    public Game deleteReviewFromGame(long gameId, int reviewRating) {
        return gameDao.deleteReviewFromGame(gameId,reviewRating).orElseThrow(GameNotFoundException::new);
    }

    @Transactional
    @Override
    public Game updateReviewFromGame(long gameId, int oldRating, int newRating) {
        return gameDao.modifyReview(gameId, oldRating, newRating).orElseThrow(GameNotFoundException::new);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Game> getRecommendationsOfGamesForUser(long userId) {
        User user = userService.getUserById(userId).orElseThrow(UserNotFoundException::new);
        if(!user.hasPreferencesSet()){
            return new ArrayList<>();
        }
        Set <Genre> userPreferences = user.getPreferences();
        List<Integer> preferencesIds = userPreferences.stream().map(Genre::getId).collect(Collectors.toList());
        Set<Game> userReviewedGames = getGamesReviewedByUser(user.getId());
        List<Long> idsToExclude = userReviewedGames.stream().map(Game::getId).collect(Collectors.toList());

        return gameDao.getRecommendationsForUser(preferencesIds,idsToExclude);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Game> getGamesReviewedByUser(long userId) {
        return gameDao.getGamesReviewedByUser(userId).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    @Override
    public Game acceptGame(long gameId, long approvingUserId) {
        Game game = gameDao.setSuggestedFalse(gameId).orElseThrow(GameNotFoundException::new);
        User suggester = game.getSuggestedBy();
        if(suggester != null) {
            mailingService.sendAcceptedSuggestionEmail(game, suggester);
            missionService.addMissionProgress(suggester.getId(), Mission.ACCEPTED_GAMES, 1f);
        }
        missionService.addMissionProgress(approvingUserId, Mission.MANAGE_GAME_SUBMISSIONS, 1f);
        return game;
    }

    @Transactional
    @Override
    public boolean rejectGame(long gameId, long approvingUserId) {
        Game rejectedGame = gameDao.getById(gameId).orElseThrow(GameNotFoundException::new);
        User suggester = rejectedGame.getSuggestedBy();
        boolean deleted = gameDao.deleteGame(gameId);
        if(deleted && rejectedGame.getSuggestedBy() != null) {
            mailingService.sendDeclinedSuggestionEmail(rejectedGame, suggester);
        }
        missionService.addMissionProgress(approvingUserId, Mission.MANAGE_GAME_SUBMISSIONS, 1f);
        return deleted;
    }
}
