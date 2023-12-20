package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.SaveReviewDTO;
import ar.edu.itba.paw.dtos.filtering.ReviewFilter;
import ar.edu.itba.paw.dtos.filtering.ReviewFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
import ar.edu.itba.paw.servicesinterfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Lazy
@Service
public class ReviewServiceImpl implements ReviewService {
    private static final int MINFAVORITEGAMERATING = 7;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);
    private final ReviewDao reviewDao;
    private final UserService userService;
    private final GameService gameService;
    private final ReportService reportService;
    private final MailingService mailingService;
    private final MissionService missionService;


    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao, UserService userService, GameService gameService, ReportService reportService, MailingService mailingService, MissionService missionService) {
        this.reviewDao = reviewDao;
        this.userService = userService;
        this.gameService = gameService;
        this.reportService = reportService;
        this.mailingService = mailingService;
        this.missionService = missionService;
    }

    @Transactional
    @Override
    public Review createReview(String title,
                        String content,
                        int rating,
                        long authorId,
                        long gameId,
                        Difficulty difficulty,
                        Double gameLength,
                        Platform platform,
                        Boolean completed,
                        Boolean replayable) {
        User author = userService.getUserById(authorId).orElseThrow(UserNotFoundException::new);
        Game reviewedGame = gameService.getGameById(gameId,authorId).orElseThrow(GameNotFoundException::new);

        if (getReviewOfUserForGame(authorId, gameId).isPresent()) {
            throw new ReviewAlreadyExistsException(reviewedGame);
        }
        Review review = reviewDao.create(title, content, rating, reviewedGame, author, difficulty, gameLength, platform, completed, replayable);
        LOGGER.info("Creating review - Game: {}, Author: {}, Title: {}, Rating: {}",author.getUsername(),reviewedGame.getName(),title,rating);
        gameService.addNewReviewToGame(reviewedGame.getId(), rating);

        missionService.addMissionProgress(author.getId(), Mission.REVIEWS_GOAL, 1f);
        Paginated<User> authorFollowers = userService.getFollowers(author.getId(), Page.with(1, 1000));

        for (User follower : authorFollowers.getList()) {
            if(userService.isNotificationEnabled(follower.getId(), NotificationType.USER_I_FOLLOW_WRITES_REVIEW)) {
                mailingService.sendReviewCreatedEmail(review, author, follower);
            }
        }
        return review;
    }

    @Transactional
    @Override
    public Review updateReview(long id,
                               String title,
                               String content,
                               int rating,
                               Difficulty difficulty,
                               Double gameLength,
                               Platform platform,
                               Boolean completed,
                               Boolean replayable) {
        Review review = reviewDao.findById(id, null).orElseThrow(ReviewNotFoundException::new);

        Review modifiedReview = reviewDao.update(id,
                new SaveReviewDTO(title,
                        content,
                        rating,
                        difficulty,
                        gameLength,
                        platform,
                        completed,
                        replayable)).orElseThrow(ReviewNotFoundException::new);
        LOGGER.info("Updating review - Game: {}, Author: {}, Title: {}, Rating: {}",review.getReviewedGame().getName(),review.getAuthor().getUsername(),title,rating);
        gameService.updateReviewFromGame(review.getReviewedGame().getId(), review.getRating(), rating);
        if(rating <= MINFAVORITEGAMERATING)
            userService.deleteFavoriteGame(review.getAuthor().getId(), review.getReviewedGame().getId());
        return modifiedReview;
    }

    @Transactional
    @Override
    public Optional<Review> getReviewById(long id, Long activeUserId) {
        return reviewDao.findById(id, activeUserId);
    }


    @Transactional(readOnly = true)
    @Override
    public Paginated<Review> searchReviews(Page page, ReviewFilter filter, Ordering<ReviewOrderCriteria> ordering, Long activeUserId){
        if (filter.getRecommendedFor() != null || filter.getFromFollowing() != null || filter.getNewForUser() != null) {
            if (!filter.isExclusive()) throw new ExclusiveFilterException("error.game.filter.recommended");
            if (filter.getRecommendedFor() != null) {
                return getRecommendedReviewsByUser(filter.getRecommendedFor(), page);
            } else if (filter.getFromFollowing() != null) {
                return getReviewsFromFollowingByUser(filter.getFromFollowing(), page);
            } else {
                return getNewReviewsExcludingActiveUser(page, activeUserId);
            }
        }
        return reviewDao.findAll(page, filter, ordering, activeUserId);
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<Review> getReviewsFromFollowingByUser(long userId, Page page) {
        User user = userService.getUserById(userId).orElseThrow(UserNotFoundException::new);
        List<User> followingUsers = userService.getFollowing(userId, Page.with(1, user.getFollowingCount())).getList();
        if (followingUsers.isEmpty()) {
            return new Paginated<>(page.getPageNumber(), page.getPageSize(), 0, 0, Collections.emptyList());
        }
        List<Long> followingIds = followingUsers.stream().map((User::getId)).collect(Collectors.toList());
        ReviewFilterBuilder filterBuilder = new ReviewFilterBuilder()
                .withAuthors(followingIds);
        return reviewDao.findAll(page, filterBuilder.build(), Ordering.defaultOrder(ReviewOrderCriteria.REVIEW_DATE), userId);
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<Review> getRecommendedReviewsByUser(long userId, Page page){
        User user = userService.getUserById(userId).orElseThrow(UserNotFoundException::new);
        List<Integer> preferences = user.getPreferences().stream().map(Genre::getId).collect(Collectors.toList());
        List<Long> authorsToExclude = new ArrayList<>();
        authorsToExclude.add(user.getId());
        ReviewFilterBuilder filterBuilder = new ReviewFilterBuilder()
                .withGameGenres(preferences).withAuthorGenres(preferences).withOrBetweenGenres(true).withAuthorsToExclude(authorsToExclude);
        return reviewDao.findAll(page, filterBuilder.build(), Ordering.defaultOrder(ReviewOrderCriteria.REVIEW_DATE), user.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<Review> getNewReviewsExcludingActiveUser(Page page, Long activeUserId) {
        List<Long> authorsToExclude = new ArrayList<>();
        if(activeUserId != null) {
            authorsToExclude.add(activeUserId);
        }
        ReviewFilterBuilder filterBuilder = new ReviewFilterBuilder().withAuthorsToExclude(authorsToExclude);

        return reviewDao.findAll(page, filterBuilder.build(), Ordering.defaultOrder(ReviewOrderCriteria.REVIEW_DATE), activeUserId);
    }

    private void deleteReview(Review review) {
        long id = review.getId();
        reportService.deleteReviewOfReports(id);
        reviewDao.deleteReview(id);
        gameService.deleteReviewFromGame(review.getReviewedGame().getId(), review.getRating());
        LOGGER.info("Deleting review: {}", id);

        missionService.addMissionProgress(review.getAuthor().getId(), Mission.REVIEWS_GOAL, -1f);

        if(review.getRating() > MINFAVORITEGAMERATING)
            userService.deleteFavoriteGame(review.getAuthor().getId(), review.getReviewedGame().getId());
    }

    @Transactional
    @Override
    public boolean deleteReviewById(long id, long reporterUserId) {
        Optional<Review> review = getReviewById(id, null);
        if(review.isPresent()){
            deleteReview(review.get());
            Game game = review.get().getReviewedGame();
            User author = review.get().getAuthor();
            if(userService.isNotificationEnabled(author.getId(), NotificationType.MY_REVIEW_IS_DELETED) && author.getId() != reporterUserId) {
                mailingService.sendReviewDeletedEmail(game, author);
            }
            LOGGER.info("User {} deleted Review {} ",reporterUserId,id);
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public void deleteReviewsOfGame(long gameId) {
        ReviewFilter filter = new ReviewFilterBuilder().withGameId(gameId).build();
        List<Review> gameReviews = reviewDao.findAll(filter, null, null);
        for (Review review : gameReviews) {
            deleteReview(review);
        }
        LOGGER.info("Deleting reviews of game: {}", gameId);
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<Review> getUserReviews(Page page, long userId, Long activeUserId){
        List<Long> authors = new ArrayList<>();
        authors.add(userId);
        return reviewDao.findAll(page, new ReviewFilterBuilder().withAuthors(authors).build(), Ordering.defaultOrder(ReviewOrderCriteria.REVIEW_DATE), activeUserId);
    }

    @Transactional
    @Override
    public ReviewFeedback updateOrCreateReviewFeedback(long reviewId, long userId, FeedbackType feedback){
        User user = userService.getUserById(userId).orElseThrow(UserNotFoundException::new);
        Review review = getReviewById(reviewId, null).orElseThrow(ReviewNotFoundException::new);

        if (Objects.equals(review.getAuthor().getId(), user.getId())) {
            throw new UserIsAuthorException();
        }
        
        FeedbackType oldFeedback = reviewDao.getReviewFeedback(review.getId(), user.getId()).orElse(null);

        Optional<ReviewFeedback> updatedFeedback = (oldFeedback == null) ? reviewDao.addReviewFeedback(review.getId(), user.getId(), feedback) :
                reviewDao.editReviewFeedback(review.getId(), user.getId(), oldFeedback, feedback);
        if (!updatedFeedback.isPresent()) {
            return null;
        }

        int userReputationOffset = (oldFeedback == null) ? ((feedback == FeedbackType.LIKE) ? 1 : -1) : ((feedback == FeedbackType.LIKE) ? 2 : -2);

        userService.modifyUserReputation(review.getAuthor().getId(), userReputationOffset);
        missionService.addMissionProgress(review.getAuthor().getId(), Mission.REPUTATION_GOAL, (float)userReputationOffset);
        return updatedFeedback.get();
    }

    @Transactional
    @Override
    public boolean deleteReviewFeedback(long reviewId, long userId){
        User user = userService.getUserById(userId).orElseThrow(UserNotFoundException::new);
        Review review = getReviewById(reviewId, null).orElseThrow(ReviewNotFoundException::new);

        FeedbackType oldFeedback = reviewDao.getReviewFeedback(review.getId(), user.getId()).orElse(null);
        return deleteReviewFeedbackAuxiliar(review, user, oldFeedback);
    }

    private boolean deleteReviewFeedbackAuxiliar(Review review, User user, FeedbackType oldFeedback) {
        if (oldFeedback == null) {
            return false;
        }
        boolean response = reviewDao.deleteReviewFeedback(review.getId(), user.getId(), oldFeedback);
        int reputationOffset = (oldFeedback == FeedbackType.LIKE) ? -1 : 1;
        userService.modifyUserReputation(review.getAuthor().getId(), reputationOffset);
        missionService.addMissionProgress(review.getAuthor().getId(), Mission.REPUTATION_GOAL, (float) reputationOffset);
        return response;
    }

    @Transactional
    @Override
    public List<Review> getAllReviewsFromGame(long gameId, Long activeUserId) {
        ReviewFilter filter = new ReviewFilterBuilder().withGameId(gameId).build();
        return reviewDao.findAll(filter, Ordering.defaultOrder(ReviewOrderCriteria.REVIEW_DATE), activeUserId);
    }

    @Transactional
    @Override
    public Optional<Review> getReviewOfUserForGame(long userId, long gameId) {
        ReviewFilter filter = new ReviewFilterBuilder().withGameId(gameId).withAuthors(Arrays.asList(userId)).build();
        Paginated<Review> review = reviewDao.findAll(Page.with(1,1),filter, null, null);
        return review.getList().stream().findFirst();
    }
}
