package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.dtos.ReviewFilter;
import ar.edu.itba.paw.models.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review createReview(String title, String content, Integer rating, String userEmail, Long gameId);
    Optional<Review> getReviewById(Long id);
    List<Review> getAllReviews(ReviewFilter filter);
}
