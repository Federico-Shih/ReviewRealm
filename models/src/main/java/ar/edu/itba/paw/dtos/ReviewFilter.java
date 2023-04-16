package ar.edu.itba.paw.dtos;

import java.util.List;

public class ReviewFilter {
    private final List<Integer> gameGenresFilter;
    private final List<Integer> reviewerPreferencesFilter;
    private final ReviewOrderCriteria reviewOrderCriteria;
    private final OrderDirection orderDirection;

    public ReviewFilter(List<Integer> gameGenresFilter, List<Integer> reviewerPreferencesFilter, ReviewOrderCriteria reviewOrderCriteria, OrderDirection orderDirection) {
        this.gameGenresFilter = gameGenresFilter;
        this.reviewerPreferencesFilter = reviewerPreferencesFilter;
        this.reviewOrderCriteria = reviewOrderCriteria;
        this.orderDirection = orderDirection;
    }

    public ReviewFilter() {
        this(null, null, ReviewOrderCriteria.REVIEW_DATE, OrderDirection.DESCENDING);
    }

    public List<Integer> getGameGenresFilter() {
        return gameGenresFilter;
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
