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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Lazy
@Service
public class ReviewServiceImpl implements ReviewService {
    private static final int MINFAVORITEGAMERATING = 7;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);
    private final ReviewDao reviewDao;
    private final UserService userService;
    private final GameService gameService;
    private final MailingService mailingService;

    private final MissionService missionService;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao, UserService userService, GameService gameService, MailingService mailingService, MissionService missionService) {
        this.reviewDao = reviewDao;
        this.userService = userService;
        this.gameService = gameService;
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
        Game reviewedGame = gameService.getGameById(gameId).orElseThrow(GameNotFoundException::new);

        if (getReviewOfUserForGame(authorId, gameId).isPresent()) {
            throw new ReviewAlreadyExistsException(reviewedGame);
        }
        Review review = reviewDao.create(title, content, rating, reviewedGame, author, difficulty, gameLength, platform, completed, replayable);
        LOGGER.info("Creating review - Game: {}, Author: {}, Title: {}, Rating: {}",author.getUsername(),reviewedGame.getName(),title,rating);
        gameService.addNewReviewToGame(reviewedGame.getId(), rating);

        missionService.addMissionProgress(author.getId(), Mission.REVIEWS_GOAL, 1f);
        List<User> authorFollowers = userService.getFollowers(author.getId());

        for (User follower : authorFollowers) {
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
        return reviewDao.findAll(page, filter, ordering, activeUserId);
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<Review> getReviewsFromFollowingByUser(long userId, Page page) {
        List<User> followingUsers = userService.getFollowing(userId);
        if(followingUsers.isEmpty()){
            return new Paginated<>(page.getPageNumber(),page.getPageSize(),0, new ArrayList<>());
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

    @Transactional
    @Override
    public boolean deleteReviewById(long id) {
        Optional<Review> review = getReviewById(id, null);
        if(review.isPresent()){
            reviewDao.deleteReview(id);
            gameService.deleteReviewFromGame(review.get().getReviewedGame().getId(), review.get().getRating());
            LOGGER.info("Deleting review: {}", id);

            Game game = review.get().getReviewedGame();
            User author = review.get().getAuthor();
            missionService.addMissionProgress(author.getId(), Mission.REVIEWS_GOAL, -1f);

            if(userService.isNotificationEnabled(author.getId(), NotificationType.MY_REVIEW_IS_DELETED)) {
                mailingService.sendReviewDeletedEmail(game, author);
            }

            if(review.get().getRating() > MINFAVORITEGAMERATING)
                userService.deleteFavoriteGame(review.get().getAuthor().getId(), review.get().getReviewedGame().getId());

            return true;
        }
        return false;
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
        
        FeedbackType oldFeedback = reviewDao.getReviewFeedback(review.getId(), user.getId()).orElse(null);
        if (oldFeedback == feedback) {
            boolean deleted = deleteReviewFeedback(review, user, oldFeedback);
            LOGGER.info("User {} deleted review {} feedback: {}", userId, reviewId, deleted);
            return null;
        }
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

    private boolean deleteReviewFeedback(Review review, User user, FeedbackType oldFeedback) {
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
    public Paginated<Review> getReviewsFromGame(Page page, long gameId, Long activeUserId, boolean excludeActiveUser) {
        ReviewFilterBuilder filterBuilder = new ReviewFilterBuilder().withGameId(gameId);
        if(excludeActiveUser && activeUserId!=null)
            filterBuilder = filterBuilder.withAuthorsToExclude(Arrays.asList(activeUserId));
        return reviewDao.findAll(page, filterBuilder.build(), Ordering.defaultOrder(ReviewOrderCriteria.REVIEW_DATE), activeUserId);
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
