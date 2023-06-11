package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.SaveReviewDTO;
import ar.edu.itba.paw.dtos.filtering.ReviewFilter;
import ar.edu.itba.paw.dtos.filtering.ReviewFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.exceptions.ReviewAlreadyExistsException;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
import ar.edu.itba.paw.servicesinterfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
                               Integer rating,
                               User author,
                               Game reviewedGame,
                               Difficulty difficulty,
                               Double gameLength,
                               Platform platform,
                               Boolean completed,
                               Boolean replayable) {
        List<Review> reviews = author.getReviews();
        if (reviews.stream().anyMatch((review) -> review.getReviewedGame().equals(reviewedGame))) {
            throw new ReviewAlreadyExistsException(reviewedGame);
        }
        Review review = reviewDao.create(title, content, rating, reviewedGame, author, difficulty, gameLength, platform, completed, replayable);
        LOGGER.info("Creating review - Game: {}, Author: {}, Title: {}, Rating: {}",author.getUsername(),reviewedGame.getName(),title,rating);
        gameService.addNewReviewToGame(reviewedGame.getId(), rating);

        missionService.addMissionProgress(author, Mission.REVIEWS_GOAL, 1f);
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
    public int updateReview(Long id, String title,
                            String content,
                            Integer rating,
                            Difficulty difficulty,
                            Double gameLength,
                            Platform platform,
                            Boolean completed,
                            Boolean replayable) {
        Optional<Review> review = reviewDao.findById(id, null);
        if(!review.isPresent()){
            return 0;
        }
        int response = reviewDao.update(id,
                new SaveReviewDTO(title,
                        content,
                        rating,
                        difficulty,
                        gameLength,
                        platform,
                        completed,
                        replayable));
        gameService.updateReviewFromGame(review.get().getReviewedGame().getId(), review.get().getRating(), rating);
        if(rating <= MINFAVORITEGAMERATING)
            gameService.deleteFavoriteGame(review.get().getAuthor().getId(), review.get().getReviewedGame().getId());
        return response;
    }

    @Transactional
    @Override
    public Optional<Review> getReviewById(Long id, User activeUser) {
        return reviewDao.findById(id, (activeUser != null)? activeUser.getId() : null);
    }


    @Transactional(readOnly = true)
    @Override
    public Paginated<Review> searchReviews(Page page, ReviewFilter searchFilter, Ordering<ReviewOrderCriteria> ordering, User activeUser) {
        return reviewDao.findAll(page, searchFilter, ordering, (activeUser != null) ? activeUser.getId() : null);
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<Review> getReviewsFromFollowingByUser(Long userId, Page page) {
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
    public Paginated<Review> getRecommendedReviewsByUser(User user, Page page) {
        List<Integer> preferences = user.getPreferences().stream().map(Genre::getId).collect(Collectors.toList());
        List<Long> authorsToExclude = new ArrayList<>();
        authorsToExclude.add(user.getId());
        ReviewFilterBuilder filterBuilder = new ReviewFilterBuilder()
                .withGameGenres(preferences).withAuthorGenres(preferences).withOrBetweenGenres(true).withAuthorsToExclude(authorsToExclude);
        return reviewDao.findAll(page, filterBuilder.build(), Ordering.defaultOrder(ReviewOrderCriteria.REVIEW_DATE), user.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<Review> getNewReviewsExcludingActiveUser(Page page, User activeUser) {
        List<Long> authorsToExclude = new ArrayList<>();
        if(activeUser != null){
            authorsToExclude.add(activeUser.getId());
        }
        ReviewFilterBuilder filterBuilder = new ReviewFilterBuilder().withAuthorsToExclude(authorsToExclude);

        return reviewDao.findAll(page, filterBuilder.build(), Ordering.defaultOrder(ReviewOrderCriteria.REVIEW_DATE), (activeUser != null) ? activeUser.getId() : null);
    }

    @Transactional
    @Override
    public boolean deleteReviewById(Long id) {
        Optional<Review> review = getReviewById(id, null);
        if(review.isPresent()){
            reviewDao.deleteReview(id);
            gameService.deleteReviewFromGame(review.get().getReviewedGame().getId(), review.get().getRating());
            LOGGER.info("Deleting review: {}", id);

            Game game = review.get().getReviewedGame();
            User author = review.get().getAuthor();
            missionService.addMissionProgress(author, Mission.REVIEWS_GOAL, -1f);

            if(userService.isNotificationEnabled(author.getId(), NotificationType.MY_REVIEW_IS_DELETED)) {
                mailingService.sendReviewDeletedEmail(game, author);
            }

            if(review.get().getRating() > MINFAVORITEGAMERATING)
                gameService.deleteFavoriteGame(review.get().getAuthor().getId(), review.get().getReviewedGame().getId());

            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<Review> getUserReviews(Page page, Long userId, User activeUser) {
        List<Long> authors = new ArrayList<>();
        authors.add(userId);
        return reviewDao.findAll(page, new ReviewFilterBuilder().withAuthors(authors).build(), Ordering.defaultOrder(ReviewOrderCriteria.REVIEW_DATE), (activeUser != null)? activeUser.getId() : null );
    }

    @Transactional
    @Override
    public boolean updateOrCreateReviewFeedback(Review review, User user, FeedbackType feedback) {
        FeedbackType oldFeedback = reviewDao.getReviewFeedback(review.getId(), user.getId());
        if (oldFeedback == feedback) {
            return deleteReviewFeedback(review, user, oldFeedback);
        }
        boolean response = (oldFeedback == null) ? reviewDao.addReviewFeedback(review.getId(), user.getId(), feedback) :
                reviewDao.editReviewFeedback(review.getId(), user.getId(), oldFeedback, feedback);
        int userReputationOffset = (oldFeedback == null) ? ((feedback == FeedbackType.LIKE) ? 1 : -1) : ((feedback == FeedbackType.LIKE) ? 2 : -2);
        userService.modifyUserReputation(review.getAuthor().getId(), userReputationOffset);
        missionService.addMissionProgress(review.getAuthor(), Mission.REPUTATION_GOAL, (float)userReputationOffset);
        return response;
    }

    private boolean deleteReviewFeedback(Review review, User user, FeedbackType oldFeedback) {
        if (oldFeedback == null) {
            return false;
        }
        boolean response = reviewDao.deleteReviewFeedback(review.getId(), user.getId(), oldFeedback);
        int reputationOffset = (oldFeedback == FeedbackType.LIKE) ? -1 : 1;
        userService.modifyUserReputation(review.getAuthor().getId(), reputationOffset);
        missionService.addMissionProgress(review.getAuthor(), Mission.REPUTATION_GOAL, (float) reputationOffset);
        return response;
    }

    @Transactional
    @Override
    public Paginated<Review> getReviewsFromGame(Page page, Long gameId, User activeUser) {
        ReviewFilter filter = new ReviewFilterBuilder().withGameId(gameId).build();
        return reviewDao.findAll(page, filter, Ordering.defaultOrder(ReviewOrderCriteria.REVIEW_DATE), (activeUser != null)? activeUser.getId() : null);
    }

    @Transactional
    @Override
    public List<Review> getAllReviewsFromGame(Long gameId, User activeUser) {
        ReviewFilter filter = new ReviewFilterBuilder().withGameId(gameId).build();
        return reviewDao.findAll(filter, Ordering.defaultOrder(ReviewOrderCriteria.REVIEW_DATE), (activeUser != null)? activeUser.getId() : null);
    }
}
