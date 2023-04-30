package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.Filter;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.exceptions.ObjectNotFoundException;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);
    private final ReviewDao reviewDao;
    private final UserService userService;
    private final GameService gameService;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao, UserService userService, GameService gameService) {
        this.reviewDao = reviewDao;
        this.userService = userService;
        this.gameService = gameService;
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
        return review;
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
