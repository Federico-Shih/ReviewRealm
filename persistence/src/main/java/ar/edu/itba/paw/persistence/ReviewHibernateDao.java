package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.PaginationTotals;
import ar.edu.itba.paw.dtos.SaveReviewDTO;
import ar.edu.itba.paw.dtos.filtering.ReviewFilter;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.FeedbackType;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.enums.ReportState;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.keys.ReviewFeedbackId;
import ar.edu.itba.paw.persistence.helpers.DaoUtils;
import ar.edu.itba.paw.persistence.helpers.QueryBuilder;
import ar.edu.itba.paw.persistenceinterfaces.PaginationDao;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ReviewHibernateDao implements ReviewDao, PaginationDao<ReviewFilter> {
    @PersistenceContext
    private EntityManager em;

    public Review create(String title,
                         String content,
                         int rating,
                         Game reviewedGame,
                         User author,
                         Difficulty difficulty,
                         Double gameLength,
                         Platform platform,
                         Boolean completed,
                         Boolean replayable) {
        final Review review = new Review(title, content, rating, reviewedGame, author, difficulty, gameLength, platform, completed, replayable);
        em.persist(review);
        return review;
    }

    @Override
    public Optional<Review> findById(long id, Long activeUserId) {
        Review review = em.find(Review.class, id);
        if(review == null || review.getDeleted()) return Optional.empty();
        User user = em.find(User.class, activeUserId != null ? activeUserId : -1);
        if (user != null) {
            ReviewFeedback reviewFeedback = em.find(ReviewFeedback.class, new ReviewFeedbackId(user, review));
            if (reviewFeedback != null) {
                review.setFeedback(reviewFeedback.getFeedback());
            }
        }
        return Optional.of(review);
    }

    @Override
    public Optional<Review> update(long id, SaveReviewDTO reviewDTO) {
        Review review = em.find(Review.class, id);
        if (review == null || review.getDeleted()) {
            return Optional.empty();
        }
        if (reviewDTO.getTitle() != null) {
            review.setTitle(reviewDTO.getTitle());
        }
        if (reviewDTO.getContent() != null) {
            review.setContent(reviewDTO.getContent());
        }
        if (reviewDTO.getRating() != null) {
            review.setRating(reviewDTO.getRating());
        }
        if (reviewDTO.getDifficulty() != null) {
            review.setDifficulty(reviewDTO.getDifficulty());
        }
        if (reviewDTO.getReplayable() != null) {
            review.setReplayability(reviewDTO.getReplayable());
        }
        if (reviewDTO.getCompleted() != null) {
            review.setCompleted(reviewDTO.getCompleted());
        }
        if (reviewDTO.getPlatform() != null) {
            review.setPlatform(reviewDTO.getPlatform());
        }
        if (reviewDTO.getGameLength() != null) {
            review.setGameLength(reviewDTO.getGameLength());
        }
        return Optional.of(review);
    }

    private QueryBuilder getQueryBuilderFromFilter(ReviewFilter filter) {
        QueryBuilder queryBuilder = new QueryBuilder().PARENTHESIS_OPEN()
                .withList("gg.genreid", filter.getFilterGameGenres());

        queryBuilder = (filter.getOrBetweenGenres() != null && filter.getOrBetweenGenres())? queryBuilder.OR():queryBuilder;
        queryBuilder = queryBuilder.withList("ap.genreid", filter.getAuthorPreferences())
                .PARENTHESIS_CLOSE();
        queryBuilder= queryBuilder.NOT().withList("r.authorid",filter.getAuthorsToExclude())
                .NOT().withList("r.gameid",filter.getGamesToExclude())
                .PARENTHESIS_OPEN()
                .withSimilar("r.content", filter.getReviewContent())
                .OR()
                .withSimilar("r.title", filter.getReviewContent())
                .OR()
                .withSimilar("g.name", filter.getReviewContent())
                .PARENTHESIS_CLOSE()
                .AND()
                .withList("r.authorid", filter.getAuthors())
                .withExact("r.gameid", filter.getGameId())
                .withExact("r.completed", filter.getCompleted())
                .withExact("r.replayability", filter.getReplayable())
                .withExact("r.deleted", filter.getDeleted());
        if (filter.getMinTimePlayed() != null) {
            queryBuilder = queryBuilder.withGreaterOrEqual("COALESCE(r.gamelength, 0)", 3600 * filter.getMinTimePlayed());
        }
        if (filter.getPlatforms() != null) {
            queryBuilder = queryBuilder.withList("r.platform", filter.getPlatforms().stream().map(Platform::name).collect(Collectors.toList()));
        }
        if (filter.getDifficulties() != null) {
            queryBuilder = queryBuilder.withList("r.difficulty", filter.getDifficulties().stream().map(Difficulty::name).collect(Collectors.toList()));
        }
        return queryBuilder;
    }

    @Override
    public Paginated<Review> findAll(Page pagination, ReviewFilter filter, Ordering<ReviewOrderCriteria> ordering, Long activeUserId) {
        PaginationTotals totals = getPaginationTotals(filter, pagination.getPageSize());
        if (pagination.getPageNumber() > totals.getTotalPages() || pagination.getPageNumber() <= 0) {
            return new Paginated<>(pagination.getPageNumber(), pagination.getPageSize(), totals.getTotalPages(), totals.getTotalElements(), Collections.emptyList());
        }
        QueryBuilder queryBuilder = getQueryBuilderFromFilter(filter);
        Query nativeQuery = em.createNativeQuery(
                "SELECT reviewid FROM ("+ "SELECT distinct r.id as reviewid, r.createddate, r.rating, r.likes + r.dislikes as controversial, r.likes - r.dislikes as popularity FROM "
                        + toTableString(filter)
                        + queryBuilder.toQuery() + ") as review"
                        + DaoUtils.toOrderString(ordering, true,"reviewid"));

        DaoUtils.setNativeParameters(queryBuilder, nativeQuery);
        nativeQuery.setMaxResults(pagination.getPageSize());
        nativeQuery.setFirstResult(pagination.getOffset());

        @SuppressWarnings("unchecked")
        final List<Long> idlist = (List<Long>) nativeQuery.getResultList().stream().map(n -> ((Number) n).longValue()).collect(Collectors.toList());

        final TypedQuery<Review> query = em.createQuery("from Review WHERE id IN :ids " + DaoUtils.toOrderString(ordering, false,null), Review.class);
        query.setParameter("ids", idlist);
        User currentUser = em.find(User.class, activeUserId != null ? activeUserId : -1);
        List<Review> reviewList = query.getResultList();
        if (currentUser != null) {
            Map<Long, FeedbackType> feedbackTypeMap = currentUser
                    .getReviewFeedbackList()
                    .stream()
                    .collect(Collectors.toMap(reviewFeedback -> reviewFeedback.getReview().getId(), (ReviewFeedback::getFeedback)));
            for (Review review : reviewList) {
                review.setFeedback(feedbackTypeMap.get(review.getId()));
            }
        }
        return new Paginated<>(pagination.getPageNumber(), pagination.getPageSize(), totals.getTotalPages(), totals.getTotalElements(), reviewList);
    }

    public List<Review> findAll(ReviewFilter filter, Ordering<ReviewOrderCriteria> ordering, Long activeUserId) {
        Page page = Page.with(1, (int) count(filter));
        if (page.getPageSize() <= 0) {
            return Collections.emptyList();
        }
        return findAll(page, filter, ordering, activeUserId).getList();
    }

    @Override
    public long count(ReviewFilter filter) {
        QueryBuilder queryBuilder = getQueryBuilderFromFilter(filter);
        Query nativeQuery = em.createNativeQuery("SELECT count(distinct r.id) FROM " + toTableString(filter) + queryBuilder.toQuery());
        DaoUtils.setNativeParameters(queryBuilder, nativeQuery);

        return ((Number)nativeQuery.getSingleResult()).longValue();
    }

    @Override
    public boolean deleteReview(long id) {
        Review review = em.find(Review.class, id);
        if(review == null || review.getDeleted()) return false;
        review.setDeleted(true);
        return true;
    }

    @Override
    public Optional<FeedbackType> getReviewFeedback(long reviewId, long userId) {
        User user = em.find(User.class, userId);
        Review review = em.find(Review.class, reviewId);
        if(review == null || review.getDeleted())
            return Optional.empty();
        ReviewFeedback feedback = em.find(ReviewFeedback.class, new ReviewFeedbackId(user, review));
        if(feedback == null) return Optional.empty();
        return Optional.of(feedback.getFeedback());
    }

    @Override
    public Optional<ReviewFeedback> editReviewFeedback(long reviewId, long userId, FeedbackType oldFeedback, FeedbackType feedback) {
        Review review = em.find(Review.class, reviewId);
        ReviewFeedback reviewFeedback = em.find(ReviewFeedback.class, new ReviewFeedbackId(em.find(User.class, userId), review));
        if(reviewFeedback == null || review == null || review.getDeleted()) {
            return Optional.empty();
        }
        if (oldFeedback != feedback) {
            reviewFeedback.setFeedback(feedback);
            Long currentLikes = review.getLikes();
            Long currentDislikes = review.getDislikes();
            review.setLikes(currentLikes + (oldFeedback == FeedbackType.LIKE ? -1 : 1));
            review.setDislikes(currentDislikes + (oldFeedback == FeedbackType.DISLIKE ? -1 : 1));
        }
        return Optional.of(reviewFeedback);
    }

    @Override
    public Optional<ReviewFeedback> addReviewFeedback(long reviewId, long userId, FeedbackType feedback) {
        Review likedReview = em.find(Review.class, reviewId);
        User likedUser = em.find(User.class, userId);
        if (likedReview == null || likedReview.getDeleted() || likedUser == null) return Optional.empty();
        ReviewFeedback possibleFeedback = em.find(ReviewFeedback.class, new ReviewFeedbackId(likedUser, likedReview));
        if (possibleFeedback != null) return Optional.empty();
        ReviewFeedback reviewFeedback = new ReviewFeedback(likedUser, likedReview, feedback);
        em.persist(reviewFeedback);

        if (feedback == FeedbackType.LIKE) {
            likedReview.setLikes(likedReview.getLikes() + 1);
        } else {
            likedReview.setDislikes(likedReview.getDislikes() + 1);
        }
        em.persist(likedReview);
        return Optional.of(reviewFeedback);
    }

    @Override
    public boolean deleteReviewFeedback(long reviewId, long userId, FeedbackType oldFeedback) {
        Review likedReview = em.find(Review.class, reviewId);
        User likedUser = em.find(User.class, userId);
        if (likedReview == null || likedReview.getDeleted() || likedUser == null) return false;
        ReviewFeedback possibleFeedback = em.find(ReviewFeedback.class, new ReviewFeedbackId(likedUser, likedReview));
        if (possibleFeedback == null) return false;
        em.remove(possibleFeedback);
        if (oldFeedback == FeedbackType.LIKE) {
            likedReview.setLikes(likedReview.getLikes() - 1);
        } else {
            likedReview.setDislikes(likedReview.getDislikes() - 1);
        }
        return true;
    }

    private String toTableString(ReviewFilter filter) {
        StringBuilder str = new StringBuilder();
        str.append("reviews as r JOIN games as g ON g.id = r.gameid JOIN users as u ON u.id = r.authorid ");
        if (filter.getFilterGameGenres() != null && !filter.getFilterGameGenres().isEmpty()) {
            str.append("JOIN genreforgames as gg ON r.gameid = gg.gameid ");
        }
        if (filter.getAuthorPreferences() != null && !filter.getAuthorPreferences().isEmpty()) {
            str.append("JOIN genreforusers as ap ON ap.userid = r.authorid ");
        }
        return str.toString();
    }
}
