package ar.edu.itba.paw.webapp.controller.querycontainers;

import ar.edu.itba.paw.dtos.filtering.GameFilter;
import ar.edu.itba.paw.dtos.filtering.GameFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;

import javax.validation.constraints.Pattern;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import java.util.List;

public class GameSearchQuery extends PaginatedQuery{

    @QueryParam("search")
    private String search;

    @Pattern(regexp ="^(name|averageRating|publishDate)$", message = "Pattern.searchGames.sort")
    @QueryParam("sort")
    private String orderCriteria;

    @Pattern(regexp ="^(asc|desc)$", message = "Pattern.search.direction")
    @QueryParam("direction")
    private String orderDirection;

    @QueryParam("genres")
    private List<Integer> genres;

    @Pattern(regexp = "^(([0-9]*[.])?[0-9]+t([0-9]*[.])?[0-9]+)$", message = "Pattern.searchGames.rating")
    @QueryParam("rating")
    private String rating;

    @QueryParam("excludeNoRating")
    @DefaultValue("false")
    private boolean excludeNoRating;

    @QueryParam("suggested")
    @DefaultValue("false")
    private boolean suggested;

    @QueryParam("recommendedFor")
    private Long recommendedFor;

    @QueryParam("favoriteOf")
    private Long favoriteOf;

    @QueryParam("notReviewedBy")
    private Long notReviewedBy;

    public GameSearchQuery() {
    }

    public Ordering<GameOrderCriteria> getOrdering(){
        OrderDirection direction = OrderDirection.fromString(orderDirection != null ? orderDirection : "");
        GameOrderCriteria criteria = GameOrderCriteria.fromString(orderCriteria != null ? orderCriteria : "");
        return new Ordering<>(
                direction != null ? direction : OrderDirection.DESCENDING,
                criteria != null ? criteria : GameOrderCriteria.PUBLISH_DATE
        );
    }
    public GameFilter getFilter() {
        float minRating = 1f;
        float maxRating = 10f;
        if (rating != null) {
            try {
                String[] ratingFilterArray = rating.split("t");
                minRating = Float.parseFloat(ratingFilterArray[0]);
                maxRating = Float.parseFloat(ratingFilterArray[1]);
            } catch (Exception ignored) {
            }
        }
        return new GameFilterBuilder()
                .withGameContent(search)
                .withGameGenres(genres)
                .withSuggestion(suggested)
                .withRatingRange(minRating, maxRating, !excludeNoRating)
                .withFavoriteGamesOf(favoriteOf)
                .withRecommendedFor(recommendedFor)
                .withNotReviewedBy(notReviewedBy)
                .build();
    }

    public Boolean getSuggested() {
        return suggested;
    }
}
