package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.GameFilter;
import ar.edu.itba.paw.dtos.filtering.GameFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.saving.SubmitGameDTO;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Mission;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import ar.edu.itba.paw.servicesinterfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Game createGame(SubmitGameDTO gameDTO, long userId) {
        Image img = imgService.uploadImage(gameDTO.getImageData(), gameDTO.getMediatype());
        if (img == null) {
            throw new RuntimeException("Error creating image"); //TODO:Cambiarlo a otra exception
        }

        User user = userService
                .getUserById(userId)
                .orElseThrow(UserNotFoundException::new);

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
        return  toReturn;
    }

    @Transactional
    @Override
    public boolean deleteGame(long gameId) {
        reviewService.deleteReviewsOfGame(gameId);
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
                    throw new GenreNotFoundException(c);
                }
            }
        }
        return genreList;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Game> getGameById(long id,Long userId) {
        Optional<Game> game =  gameDao.getById(id,userId);
        if(!game.isPresent()){
            return game;
        }
        if(game.get().getSuggestion()){
            if(userId == null){
                return Optional.empty();
            }
            Optional<User> user = userService.getUserById(userId);
            if(!user.isPresent() || !user.get().getRoles().contains(RoleType.MODERATOR)){
                return Optional.empty();
            }
        }
        return game;
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<Game> searchGames(Page page, GameFilter searchFilter, Ordering<GameOrderCriteria> ordering, Long userId)
    {
        if(searchFilter.getFavoriteGamesOf() != null){
            if(searchFilter.isProperFavoriteOf()){
                return userService.getFavoriteGamesFromUser(page,searchFilter.getFavoriteGamesOf());
            }
            throw new ExclusiveFilterException("error.game.filter.favorite");
        }
        if(searchFilter.getRecommendedFor() != null){
            if(searchFilter.isProperRecommendedFor()){
                return getRecommendationsOfGamesForUser(page,searchFilter.getRecommendedFor());
            }
            throw new ExclusiveFilterException("error.game.filter.recommended");
        }
        if(searchFilter.getNotReviewedBy() != null){
            if(searchFilter.isProperNotReviewedBy()){
                return searchGamesNotReviewedByUser(page, searchFilter.getGameContent(), ordering, searchFilter.getNotReviewedBy());
            }
            throw new ExclusiveFilterException("error.game.filter.notreviewed");
        }
        return gameDao.findAll(page, searchFilter, ordering,userId);
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<Game> searchGamesNotReviewedByUser(Page page, String search, Ordering<GameOrderCriteria> ordering, long userId) {
        List<Long> gamesReviewed = getGamesReviewedByUser(userId).stream().map(Game::getId).collect(Collectors.toList());
        GameFilter gameFilter = new GameFilterBuilder().withGameContent(search).withGamesToExclude(gamesReviewed).build();
        return gameDao.findAll(page, gameFilter, ordering,userId);
    }


    @Transactional(readOnly = true)
    @Override
    public GameReviewData getGameReviewDataByGameId(long id) {
        if(!gameDao.getById(id).isPresent())
            throw new GameNotFoundException();
        List<Review> reviews = reviewService.getAllReviewsFromGame(id,null);
        if(!reviews.isEmpty()) {
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
    public Paginated<Game> getRecommendationsOfGamesForUser(Page page,long userId) {
        User user = userService.getUserById(userId).orElseThrow(UserNotFoundException::new);
        if(!user.hasPreferencesSet()){
            return new Paginated<>(1,0,0,0,new ArrayList<>());
        }
        Set <Genre> userPreferences = user.getPreferences();
        List<Integer> preferencesIds = userPreferences.stream().map(Genre::getId).collect(Collectors.toList());
        Set<Game> userReviewedGames = getGamesReviewedByUser(user.getId());
        List<Long> idsToExclude = userReviewedGames.stream().map(Game::getId).collect(Collectors.toList());
        GameFilter filter = new GameFilterBuilder().withGameGenres(preferencesIds).withSuggestion(false).withGamesToExclude(idsToExclude).build();
        Ordering<GameOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING);
        return gameDao.findAll(page,filter,ordering,userId);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Game> getGamesReviewedByUser(long userId) {
        return gameDao.getGamesReviewedByUser(userId).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    @Override
    public Game acceptGame(long gameId, long approvingUserId) {
        Game game = gameDao.getById(gameId).orElseThrow(GameNotFoundException::new);
        if(!game.getSuggestion()){
            throw new GameSuggestionAlreadyHandled();
        }
        gameDao.setSuggestedFalse(gameId);
        User suggester = game.getSuggestedBy();
        LOGGER.info("Accepting game - id: {} ; name: {}",gameId, game.getName());
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
        if(!rejectedGame.getSuggestion()){
            throw new GameSuggestionAlreadyHandled();
        }
        LOGGER.info("Rejecting game - id: {} ; name: {}",gameId, rejectedGame.getName());
        boolean deleted = gameDao.deleteGame(gameId);
        if(deleted && rejectedGame.getSuggestedBy() != null) {
            mailingService.sendDeclinedSuggestionEmail(rejectedGame, suggester);
        }
        missionService.addMissionProgress(approvingUserId, Mission.MANAGE_GAME_SUBMISSIONS, 1f);
        return deleted;
    }
}
