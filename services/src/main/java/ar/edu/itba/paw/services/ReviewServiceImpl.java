package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.*;
import ar.edu.itba.paw.dtos.filtering.ReviewFilter;
import ar.edu.itba.paw.dtos.filtering.ReviewFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
import ar.edu.itba.paw.dtos.searching.ReviewSearchFilter;
import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistenceinterfaces.PaginationDao;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.MailingService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.ArrayList;
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

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao, UserService userService, GameService gameService, MailingService mailingService) {
        this.reviewDao = reviewDao;
        this.userService = userService;
        this.gameService = gameService;
        this.mailingService = mailingService;
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
        Review review = reviewDao.create(title, content, rating, reviewedGame, author, difficulty, gameLength, platform, completed, replayable);
        LOGGER.info("Creating review - Game: {}, Author: {}, Title: {}, Rating: {}",author.getUsername(),reviewedGame.getName(),title,rating);
        gameService.addNewReviewToGame(reviewedGame.getId(), rating);

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
    public Paginated<Review> searchReviews(Page page, ReviewSearchFilter searchFilter, Ordering<ReviewOrderCriteria> ordering, User activeUser) {
        ReviewFilter filter = new ReviewFilterBuilder()
                .withGameGenres(searchFilter.getGenres())
                .withAuthorGenres(searchFilter.getPreferences())
                .withReviewContent(searchFilter.getSearch())
                .withMinTimePlayed(searchFilter.getMinTimePlayed())
                .withPlatforms(searchFilter.getPlatforms())
                .withDifficulties(searchFilter.getDifficulties())
                .withCompleted(searchFilter.getCompleted())
                .withReviewContent(searchFilter.getSearch())
                .build();
        return reviewDao.findAll(page, filter, ordering, (activeUser != null) ? activeUser.getId() : null);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Review> getReviewsFromFollowingByUser(Long userId, Integer size) {
        if(size<=0){
            return null;
        }
        List<User> followingUsers = userService.getFollowing(userId);
        if(followingUsers.isEmpty()){
            return new ArrayList<>();
        }
        List<Long> followingIds = followingUsers.stream().map((User::getId)).collect(Collectors.toList());
        ReviewFilterBuilder filterBuilder = new ReviewFilterBuilder()
                .withAuthors(followingIds);
        return reviewDao.findAll(Page.with(1, size), filterBuilder.build(), Ordering.defaultOrder(ReviewOrderCriteria.REVIEW_DATE), userId).getList();
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
            String userEmail = author.getEmail();

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
    public boolean updateOrCreateReviewFeedback(Review review, User user, ReviewFeedback feedback) {
        ReviewFeedback oldFeedback = reviewDao.getReviewFeedback(review.getId(), user.getId());
        if(oldFeedback == feedback){
            return deleteReviewFeedback(review, user,oldFeedback);
        }
        boolean response = (oldFeedback==null)? reviewDao.addReviewFeedback(review.getId(), user.getId(), feedback):
                reviewDao.editReviewFeedback(review.getId(), user.getId(),oldFeedback,feedback);
        userService.modifyUserReputation(review.getAuthor().getId(),(feedback == ReviewFeedback.LIKE)? 1:-1);
        return response;
    }

    private boolean deleteReviewFeedback(Review review, User user, ReviewFeedback oldFeedback) {
        if(oldFeedback == null){
            return false;
        }
        boolean response = reviewDao.deleteReviewFeedback(review.getId(), user.getId(), oldFeedback);
        userService.modifyUserReputation(review.getAuthor().getId(),(oldFeedback == ReviewFeedback.LIKE)? -1:1);
        return response;
    }

    @Override
    public Paginated<Review> getReviewsFromGame(Page page, Long gameId, User activeUser) {
        ReviewFilter filter = new ReviewFilterBuilder().withGameId(gameId).build();
        return reviewDao.findAll(page, filter, Ordering.defaultOrder(ReviewOrderCriteria.REVIEW_DATE), (activeUser != null)? activeUser.getId() : null);
    }

    @Override
    public List<Review> getAllReviewsFromGame(Long gameId, User activeUser) {
        ReviewFilter filter = new ReviewFilterBuilder().withGameId(gameId).build();
        return reviewDao.findAll(filter, Ordering.defaultOrder(ReviewOrderCriteria.REVIEW_DATE), (activeUser != null)? activeUser.getId() : null);
    }
}
