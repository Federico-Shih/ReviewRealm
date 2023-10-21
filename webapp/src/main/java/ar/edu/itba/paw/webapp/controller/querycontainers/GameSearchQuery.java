package ar.edu.itba.paw.webapp.controller.querycontainers;

import ar.edu.itba.paw.dtos.filtering.GameFilter;
import ar.edu.itba.paw.dtos.filtering.GameFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.UserOrderCriteria;

import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;
import java.util.List;

//            @RequestParam(value = "page", defaultValue = "1") Integer page,
//            @RequestParam(value = "pageSize", required = false) Integer pageSize,
//            @RequestParam(value = "o-crit", defaultValue = "0") Integer orderCriteria,
//            @RequestParam(value = "o-dir", defaultValue = "0") Integer orderDirection,
//            @RequestParam(value = "f-gen", defaultValue = "") List<Integer> genresFilter,
//            @RequestParam(value = "f-rat", defaultValue = "") String ratingFilter,
//            @RequestParam(value = "f-enr", defaultValue = "") Boolean excludeNoRatingFilter,
//            @RequestParam(value = "search", defaultValue = "") String search,
//            @RequestParam(value = "created", required = false) Boolean created,
//            @RequestParam(value = "deleted", required = false) Boolean deleted
public class GameSearchQuery extends PaginatedQuery{

    @QueryParam("search")
    private String search;

    @Pattern(regexp ="^(name|averageRating|publishDate)$", flags = Pattern.Flag.CASE_INSENSITIVE)
    @QueryParam("sort")
    private String orderCriteria;

    @Pattern(regexp ="^(asc|desc)$", flags = Pattern.Flag.CASE_INSENSITIVE)
    @QueryParam("direction")
    private String orderDirection;

    @QueryParam("genres")
    private List<Integer> genres;

    @QueryParam("rating")
    private String rating;

    @QueryParam("excludeNoRating")
    private Boolean excludeNoRating;

    @QueryParam("suggested")
    private Boolean suggested;

    @QueryParam("recommendedFor")
    private Long recommendedFor;


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
    public GameFilter getFilter(){
        float minRating = 1f;
        float maxRating = 10f;
        try {
            String [] ratingFilterArray = rating.split("t");
            minRating = Float.parseFloat(ratingFilterArray[0]);
            maxRating = Float.parseFloat(ratingFilterArray[1]);
        } catch (Exception ignored) {}

        return new GameFilterBuilder().
                withGameContent(search).
                withGameGenres(genres).
                withSuggestion(suggested).
                withRatingRange(minRating,maxRating,excludeNoRating == null || !excludeNoRating)
                .build();
    }
    public boolean isRecommendedFor(){
        return recommendedFor != null && recommendedFor > 0;
    }
    public boolean isProperRecommendedFor(){
        return search == null && genres.isEmpty() && rating == null && excludeNoRating == null && suggested == null;
    }

    public Long getRecommendedFor() {
        return recommendedFor;
    }


}
