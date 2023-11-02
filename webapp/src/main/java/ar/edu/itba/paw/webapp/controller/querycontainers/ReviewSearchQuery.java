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
import ar.edu.itba.paw.webapp.controller.annotations.ExistentGameId;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentGenreList;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentPlatformList;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentUserId;
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

    @ExistentGenreList
    @QueryParam("gameGenres")
    private List<Integer> gameGenres;

    @QueryParam("excludeAuthors")
    private List<Long> authorsToExclude;

    @QueryParam("authors")
    private List<Long> authors;

    @QueryParam("timePlayedFilter")
    private String timePlayedFilter;

    @ExistentGenreList
    @QueryParam("authorPreferences")
    private List<Integer> authorPreferences;

    @ExistentPlatformList
    @QueryParam("platformsFilter")
    private List<Integer> platformsFilter;

    @ExistentGenreList
    @QueryParam("difficultyFilter")
    private List<Integer> difficultyFilter;

    @QueryParam("completedFilter")
    private Boolean completedFilter;

    @QueryParam("replayableFilter")
    private Boolean replayableFilter;

    @QueryParam("search")
    private String search;

    @ExistentGameId(optional = true)
    @QueryParam("gameId")
    private Long gameId;

    @ExistentUserId(optional = true)
    @QueryParam("recommendedFor")
    private Long recommendedFor;


    public ReviewSearchQuery() {
    }

    public String getSearch() {
        return search;
    }

    public ReviewFilter getFilter() {
        Double timePlayed = timePlayedFilter==null? null : Double.valueOf(timePlayedFilter);
        //Los gets de los enums son seguros porque se validan en las anotaciones
        List<Platform> platforms = platformsFilter == null ? null : platformsFilter.stream().map(p -> Platform.getById(p).get()
        ).collect(java.util.stream.Collectors.toList());
        List<Difficulty> difficulties = difficultyFilter == null ? null : difficultyFilter.stream().map(d -> Difficulty.getById(d).get()
        ).collect(java.util.stream.Collectors.toList());

        return new ReviewFilterBuilder()
                .withGameId(gameId)
                .withGameGenres(gameGenres)
                .withAuthorGenres(authorPreferences)
                .withAuthors(authors)
                .withAuthorsToExclude(authorsToExclude)
                .withMinTimePlayed(timePlayed)
                .withPlatforms(platforms)
                .withDifficulties(difficulties)
                .withCompleted(completedFilter)
                .withReplayable(replayableFilter)
                .withReviewContent(search)
                .withRecommendedFor(recommendedFor)
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
