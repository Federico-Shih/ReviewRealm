package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.Filter;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
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
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);
    private final ReviewDao reviewDao;
    private final UserService userService;
    private final GameService gameService;
    private final MailingService mailingService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao, UserService userService, GameService gameService, MailingService mailingService) {
        this.reviewDao = reviewDao;
        this.userService = userService;
        this.gameService = gameService;
        this.mailingService = mailingService;
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
        templateVariables.put("webBaseUrl", "http://localhost:8080/paw-2023a-04");

        Object[] stringArgs = {author.getUsername()};
        String subject = messageSource.getMessage("email.newreview.subject",
                stringArgs, LocaleContextHolder.getLocale());

        for (User follower : authorFollowers) {
            mailingService.sendEmail(follower.getEmail(), subject, "newreview", templateVariables);
        }

        if(review.getRating()>7)
            updateFavoriteGames(author.getId(), review);

        return review;
    }

    private void updateFavoriteGames(long userId, Review review) {
        Optional<Long> toDelete = Optional.empty();
        List<Review> bestReviews = reviewDao.getBestReviews(userId);
        if(bestReviews.size()==3) {
            bestReviews.sort((o1, o2) -> o2.getRating().compareTo(o1.getRating()));
            if (bestReviews.get(2).getRating() > review.getRating()) {
                return;
            }
            toDelete = Optional.of(bestReviews.remove(2).getId());
        }
        reviewDao.updateFavGames(userId, review.getId(), review.getReviewedGame().getId(), toDelete);

        /*
        List<Review> reviewList = reviewDao.getUserReviews(userId);
        reviewList.removeIf(review -> review.getRating()<=7);
        if(reviewList.stream().filter(review -> review.getRating()>rating).count()>=3)
            return;
        reviewList.sort((o1, o2) -> o2.getRating().compareTo(o1.getRating()));
        reviewDao.updateFavGames(userId,
                reviewList.subList(0,Math.min(2,reviewList.size()-1)).stream().map(
                        review -> review.getReviewedGame().getId()).collect(Collectors.toList()),
                reviewList.subList(0,Math.min(2,reviewList.size()-1)).stream().map(
                        Review::getId).collect(Collectors.toList()));
         */
    }

    @Override
    public Optional<Review> getReviewById(Long id) {
        return reviewDao.getById(id);
    }

    @Override
    public Paginated<Review> getAllReviews(Filter filter, Integer page, Integer pageSize) {
        return reviewDao.getAll(filter, page, pageSize);
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
        List<Long> followingIds = followingUsers.stream().map(User::getId).collect(Collectors.toList());
        return reviewDao.getReviewsFromFollowing(followingIds, size);
    }

    @Override
    public boolean deleteReviewById(Long id) {
        LOGGER.info("Deleting review: {}", id);
        Optional<Review> review = getReviewById(id);
        if(review.isPresent()){
            boolean op = reviewDao.deleteReview(id);
            if(op){
                gameService.deleteReviewFromGame(review.get().getReviewedGame().getId(), review.get().getRating());
            }
            return op;
        }
        return false;
    }

    @Override
    public List<Review> getUserReviews(long userId) {
        return reviewDao.getUserReviews(userId);
    }
}
