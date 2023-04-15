package ar.edu.itba.paw.dtos;

import java.util.List;

public class ReviewFilter {
    private final List<Long> gameGenresFilter;
    private final List<Long> reviewerPreferencesFilter;
    private final ReviewOrderCriteria reviewOrderCriteria;
    private final OrderDirection orderDirection;

    public ReviewFilter(List<Long> gameGenresFilter, List<Long> reviewerPreferencesFilter, ReviewOrderCriteria reviewOrderCriteria, OrderDirection orderDirection) {
        this.gameGenresFilter = gameGenresFilter;
        this.reviewerPreferencesFilter = reviewerPreferencesFilter;
        this.reviewOrderCriteria = reviewOrderCriteria;
        this.orderDirection = orderDirection;
    }

    public ReviewFilter() {
        this(null, null, ReviewOrderCriteria.REVIEW_DATE, OrderDirection.DESCENDING);
    }

    public List<Long> getGameGenresFilter() {
        return gameGenresFilter;
    }

    public List<Long> getReviewerPreferencesFilter() {
        return reviewerPreferencesFilter;
    }

    public ReviewOrderCriteria getReviewOrderCriteria() {
        return reviewOrderCriteria;
    }

    public OrderDirection getOrderDirection() {
        return orderDirection;
    }
}
