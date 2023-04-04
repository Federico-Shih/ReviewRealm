package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.ReviewFilter;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDao reviewDao;
    private final UserDao userDao;
    private final GameDao gameDao;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao, UserDao userDao, GameDao gameDao) {
        this.reviewDao = reviewDao;
        this.userDao = userDao;
        this.gameDao = gameDao;
    }

    @Override
    public Review createReview(String title, String content, Integer rating, String userEmail, Integer gameId) {
        return reviewDao.create(title, content, rating, gameDao.getById(gameId),userDao.getByEmail(userEmail));
    }

    @Override
    public Review getReviewById(Integer id) {
        return reviewDao.getById(id);
    }

    @Override
    public List<Review> getAllReviews(ReviewFilter filter) {
        return reviewDao.getAll(filter);
    }
}
