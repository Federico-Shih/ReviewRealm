package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.*;
import ar.edu.itba.paw.dtos.builders.ReviewFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.MailingService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);
    private final ReviewDao reviewDao;
    private final UserService userService;
    private final GameService gameService;
    private final MailingService mailingService;
    private final Environment env;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao, UserService userService, GameService gameService, MailingService mailingService, Environment env) {
        this.reviewDao = reviewDao;
        this.userService = userService;
        this.gameService = gameService;
        this.mailingService = mailingService;
        this.env = env;
    }


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
        gameService.addNewReviewToGame(reviewedGame.getId(), rating);

        List<User> authorFollowers = userService.getFollowers(author.getId());
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("author", author.getUsername());
        templateVariables.put("game", reviewedGame.getName());
        templateVariables.put("reviewId", review.getId());
        templateVariables.put("webBaseUrl", env.getProperty("mailing.weburl"));

        Object[] stringArgs = {author.getUsername()};
        String subject = messageSource.getMessage("email.newreview.subject",
                stringArgs, LocaleContextHolder.getLocale());

        for (User follower : authorFollowers) {
            if(userService.isNotificationEnabled(follower.getId(), NotificationType.USER_I_FOLLOW_WRITES_REVIEW)) {
                mailingService.sendEmail(follower.getEmail(), subject, "newreview", templateVariables);
            }
        }

        if(review.getRating()>7)
            updateFavoriteGames(author.getId(), review);

        return review;
    }

    private void updateFavoriteGames(long userId, Review review) {
        Optional<Long> toDelete = Optional.empty();
        List<Review> bestReviews = reviewDao.getBestReviews(userId);
        if(bestReviews.stream().anyMatch(r -> r.getReviewedGame().getId().equals(review.getReviewedGame().getId()))){
            return;
        }
        if(bestReviews.size()==3) {
            bestReviews.sort((o1, o2) -> o2.getRating().compareTo(o1.getRating()));
            if (bestReviews.get(2).getRating() > review.getRating()) {
                return;
            }
            toDelete = Optional.of(bestReviews.remove(2).getId());
        }
        reviewDao.updateFavGames(userId, review.getId(), review.getReviewedGame().getId(), toDelete);
    }

    @Override
    public Optional<Review> getReviewById(Long id, User activeUser) {
        return reviewDao.findById(id, (activeUser != null)? activeUser.getId() : null);
    }

    @Override
    public Paginated<Review> getAllReviews(Page page, ReviewFilter filter, Ordering<ReviewOrderCriteria> ordering, User activeUser) {
       return reviewDao.findAll(page, filter, ordering, (activeUser != null) ? activeUser.getId() : null);
    }

    @Override
    public List<Review> getReviewsFromFollowingByUser(Long userId, Integer size) {
        if(size<=0){
            return null;
        }
        List<User> followingUsers = userService.getFollowing(userId);
        if(followingUsers.isEmpty()){
            return new ArrayList<>();
        }
        List<Integer> followingIds = followingUsers.stream().map((user -> user.getId().intValue())).collect(Collectors.toList());
        ReviewFilterBuilder filterBuilder = new ReviewFilterBuilder()
                .withAuthors(followingIds);
        return reviewDao.findAll(Page.with(1, size), filterBuilder.build(), Ordering.defaultOrder(ReviewOrderCriteria.REVIEW_DATE), userId).getList();
    }

    @Override
    public boolean deleteReviewById(Long id) {
        LOGGER.info("Deleting review: {}", id);
        Optional<Review> review = getReviewById(id, null);
        if(review.isPresent()){
            boolean op = reviewDao.deleteReview(id);
            if(op){
                gameService.deleteReviewFromGame(review.get().getReviewedGame().getId(), review.get().getRating());
            }
            Game game = review.get().getReviewedGame();
            User author = review.get().getAuthor();
            String userEmail = author.getEmail();

            if(userService.isNotificationEnabled(author.getId(), NotificationType.MY_REVIEW_IS_DELETED)) {
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put("game", game.getName());
                templateVariables.put("gameId", game.getId());
                templateVariables.put("webBaseUrl", env.getProperty("mailing.weburl"));
                Object[] stringArgs = {};
                String subject = messageSource.getMessage("email.deletedreview.subject",
                        stringArgs, LocaleContextHolder.getLocale());

                mailingService.sendEmail(userEmail, subject, "deletedreview", templateVariables);
            }
            return op;
        }
        return false;
    }

    // TODO: paginar
    @Override
    public List<Review> getUserReviews(long userId, User activeUser) {
        List<Integer> authors = new ArrayList<>();
        authors.add((int) userId);
        return reviewDao.findAll(Page.with(1, 100), new ReviewFilterBuilder().withAuthors(authors).build(), Ordering.defaultOrder(ReviewOrderCriteria.REVIEW_DATE), (activeUser != null)? activeUser.getId() : null ).getList();
    }

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
       // ReviewFeedback oldFeedback = reviewDao.getReviewFeedback(review.getId(), user.getId());
        if(oldFeedback == null){
            return false;
        }
        boolean response = reviewDao.deleteReviewFeedback(review.getId(), user.getId(), oldFeedback);
        userService.modifyUserReputation(review.getAuthor().getId(),(oldFeedback == ReviewFeedback.LIKE)? -1:1);
        return response;
    }
}
