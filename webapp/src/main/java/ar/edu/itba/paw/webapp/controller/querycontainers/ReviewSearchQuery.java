package ar.edu.itba.paw.webapp.controller.querycontainers;

/*
//            @RequestParam(value = "search", defaultValue = "") final String search,
//            @RequestParam(value = "page", defaultValue = "1") Integer page,
//            @RequestParam(value = "pageSize", required = false) Integer pageSize,
//            @RequestParam(value = "o-crit", defaultValue = "0") Integer orderCriteria,
//            @RequestParam(value = "o-dir", defaultValue = "0") Integer orderDirection
 */

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.ReviewFilter;
import ar.edu.itba.paw.dtos.filtering.ReviewFilterBuilder;
import ar.edu.itba.paw.dtos.filtering.UserFilter;
import ar.edu.itba.paw.dtos.filtering.UserFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.*;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import org.apache.commons.lang3.builder.Diff;
import org.springframework.security.access.method.P;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;
import java.util.List;

public class ReviewSearchQuery extends PaginatedQuery {

    @Pattern(regexp ="^(asc|desc)$", flags = Pattern.Flag.CASE_INSENSITIVE)
    @QueryParam("orderCriteria")
    private String orderCriteria;

    @Pattern(regexp ="^(asc|desc)$", flags = Pattern.Flag.CASE_INSENSITIVE)
    @QueryParam("orderDirection")
    private String orderDirection;

    @QueryParam("genresFilter")
    private List<Integer> genreFilter;

    @QueryParam("timePlayedFilter")
    private String timePlayedFilter;

    @QueryParam("preferencesFilter")
    private List<Integer> preferencesFilter;

    @QueryParam("platformsFilter")
    private List<Platform> platformsFilter;

    @QueryParam("difficultyFilter")
    private List<Difficulty> difficultyFilter;

    @QueryParam("completedFilter")
    private Boolean completedFilter;

    @QueryParam("replayableFilter")
    private Boolean replayableFilter;

    @QueryParam("page")
    private Integer page;

    @QueryParam("pageSize")
    private Integer pageSize;

    @QueryParam("search")
    private String search;

    public ReviewSearchQuery() {
    }

    public String getSearch() {
        return search;
    }

    public ReviewFilter getFilter() {
        Double timePlayed = timePlayedFilter==null? null : Double.valueOf(timePlayedFilter);
        return new ReviewFilterBuilder()
                .withGameGenres(genreFilter)
                .withAuthorGenres(preferencesFilter)
                .withMinTimePlayed(timePlayed)
                .withPlatforms(platformsFilter)
                .withDifficulties(difficultyFilter)
                .withCompleted(completedFilter)
                .withReplayable(replayableFilter)
                .build();
    }


    public Ordering<ReviewOrderCriteria> getOrdering() {
        OrderDirection direction = OrderDirection.fromString(orderDirection != null ? orderDirection : "");
        ReviewOrderCriteria criteria = ReviewOrderCriteria.fromString(orderCriteria != null ? orderCriteria : "");
        return new Ordering<>(
                direction != null ? direction : OrderDirection.DESCENDING,
                criteria != null ? criteria : ReviewOrderCriteria.REVIEW_DATE
            );
    }
}
