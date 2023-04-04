package ar.edu.itba.paw.dtos;

import java.util.List;

public class ReviewFilter {
    private final List<Integer> gameGenreFilter;
    private final List<Integer> reviewerPreferencesFilter;
    private final ReviewOrderCriteria reviewOrderCriteria;
    private final OrderDirection orderDirection;

    public ReviewFilter(List<Integer> gameGenreFilter, List<Integer> reviewerPreferencesFilter, ReviewOrderCriteria reviewOrderCriteria, OrderDirection orderDirection) {
        this.gameGenreFilter = gameGenreFilter;
        this.reviewerPreferencesFilter = reviewerPreferencesFilter;
        this.reviewOrderCriteria = reviewOrderCriteria;
        this.orderDirection = orderDirection;
    }

    public ReviewFilter() {
        this(null, null, ReviewOrderCriteria.REVIEW_DATE, OrderDirection.DESCENDING);
    }

    public List<Integer> getGameGenreFilter() {
        return gameGenreFilter;
    }

    public List<Integer> getReviewerPreferencesFilter() {
        return reviewerPreferencesFilter;
    }

    public ReviewOrderCriteria getReviewOrderCriteria() {
        return reviewOrderCriteria;
    }

    public OrderDirection getOrderDirection() {
        return orderDirection;
    }
}
