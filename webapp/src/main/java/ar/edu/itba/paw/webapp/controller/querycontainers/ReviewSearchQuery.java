package ar.edu.itba.paw.webapp.controller.querycontainers;

/*
//            @RequestParam(value = "search", defaultValue = "") final String search,
//            @RequestParam(value = "page", defaultValue = "1") Integer page,
//            @RequestParam(value = "pageSize", required = false) Integer pageSize,
//            @RequestParam(value = "o-crit", defaultValue = "0") Integer orderCriteria,
//            @RequestParam(value = "o-dir", defaultValue = "0") Integer orderDirection
 */

import ar.edu.itba.paw.dtos.filtering.ReviewFilter;
import ar.edu.itba.paw.dtos.filtering.ReviewFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.*;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.webapp.controller.annotations.*;

import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;
import java.util.List;

public class ReviewSearchQuery extends PaginatedQuery {

    @Pattern(regexp ="^(created|rating|popularity|controversial)$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Pattern.searchReview.criteria")
    @QueryParam("orderCriteria")
    private String orderCriteria;

    @Pattern(regexp ="^(asc|desc)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Pattern.search.direction")
    @QueryParam("direction")
    private String orderDirection;

    @ExistentGenreList
    @QueryParam("gameGenres")
    private List<Integer> gameGenres;

    @QueryParam("excludeAuthors")
    private List<Long> authorsToExclude;

    @QueryParam("authors")
    private List<Long> authors;

    @Pattern(regexp = "^(?:[1-9]\\d*|0)?(?:\\.\\d+)?$", message = "Pattern.searchReview.timePlayedFilter")
    @QueryParam("timePlayedFilter")
    private String timePlayedFilter;

    @ExistentGenreList
    @QueryParam("authorPreferences")
    private List<Integer> authorPreferences;

    @ExistentPlatformList
    @QueryParam("platformsFilter")
    private List<String> platformsFilter;

    @ExistentDifficultyList
    @QueryParam("difficultyFilter")
    private List<String> difficultyFilter;

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
        //Los ValueOf son seguros porque ya se validaron en los validators
        List<Platform> platforms = platformsFilter == null ? null : platformsFilter.stream().map(p -> Platform.valueOf(p.toUpperCase())
        ).collect(java.util.stream.Collectors.toList());
        List<Difficulty> difficulties = difficultyFilter == null ? null : difficultyFilter.stream().map(d -> Difficulty.valueOf(d.toUpperCase())
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
