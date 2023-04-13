package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.ReviewFilter;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

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
    public Review createReview(String title, String content, Integer rating, String userEmail, Long gameId) {
        Optional<User> user = userService.getUserByEmail(userEmail);
        Optional<Game> game = gameService.getGameById(gameId);
        if (!game.isPresent()) {
            return null;
        }
        User presentUser = user.orElseGet(() -> userService.createUser(userEmail, ""));
        return reviewDao.create(title, content, rating, game.get(), presentUser);
    }

    @Override
    public Optional<Review> getReviewById(Long id) {
        return reviewDao.getById(id);
    }

    @Override
    public List<Review> getAllReviews(ReviewFilter filter) {
        return reviewDao.getAll(filter);
    }
}
