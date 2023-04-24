package ar.edu.itba.paw.dtos;

import java.util.List;

public class Filter {
    private final List<Integer> gameGenresFilter;
    private final List<Integer> reviewerPreferencesFilter;
    private final ReviewOrderCriteria reviewOrderCriteria;

    private final GameOrderCriteria gameOrderCriteria;

    private final OrderDirection orderDirection;

    public Filter(List<Integer> gameGenresFilter, List<Integer> reviewerPreferencesFilter, ReviewOrderCriteria reviewOrderCriteria, GameOrderCriteria gameOrderCriteria,
                  OrderDirection orderDirection) {
        this.gameGenresFilter = gameGenresFilter;
        this.reviewerPreferencesFilter = reviewerPreferencesFilter;
        this.reviewOrderCriteria = reviewOrderCriteria;
        this.gameOrderCriteria = gameOrderCriteria;
        this.orderDirection = orderDirection;
    }

    public static Filter ReviewFilter() {
        return new Filter(null, null, ReviewOrderCriteria.REVIEW_DATE, null,OrderDirection.DESCENDING);
    }
    public static Filter GameFilter() {
        return new Filter (null, null, null, GameOrderCriteria.PUBLISH_DATE,OrderDirection.DESCENDING);
    }

    public List<Integer> getGameGenresFilter() {return gameGenresFilter;}

    public List<Integer> getReviewerPreferencesFilter() {
        return reviewerPreferencesFilter;
    }

    public ReviewOrderCriteria getReviewOrderCriteria() {
        return reviewOrderCriteria;
    }

    public OrderDirection getOrderDirection() {
        return orderDirection;
    }

    public GameOrderCriteria getGameOrderCriteria() {return gameOrderCriteria;}
}
