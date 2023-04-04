package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.dtos.ReviewFilter;
import ar.edu.itba.paw.models.Review;

import java.util.List;

public interface ReviewService {
    Review createReview(String title, String content, Integer rating, String userEmail, Integer gameId);
    Review getReviewById(Integer id);
    List<Review> getAllReviews(ReviewFilter filter);
}
