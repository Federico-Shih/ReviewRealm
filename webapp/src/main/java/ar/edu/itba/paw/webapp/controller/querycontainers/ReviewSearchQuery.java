package ar.edu.itba.paw.webapp.controller.querycontainers;

import ar.edu.itba.paw.dtos.filtering.ReviewFilter;
import ar.edu.itba.paw.dtos.filtering.ReviewFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.webapp.controller.annotations.*;

import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;
import java.util.List;

public class ReviewSearchQuery extends PaginatedQuery {

    @Pattern(regexp = "^(created|rating|popularity|controversial)$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Pattern.searchReview.criteria")
    @QueryParam("sort")
    private String orderCriteria;

    @Pattern(regexp ="^(asc|desc)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Pattern.search.direction")
    @QueryParam("direction")
    private String orderDirection;

    @QueryParam("gameGenres")
    private List<Integer> gameGenres;

    @QueryParam("excludeAuthors")
    private List<Long> authorsToExclude;

    @QueryParam("authors")
    private List<Long> authors;

    @Pattern(regexp = "^(?:[1-9]\\d*|0)?(?:\\.\\d+)?$", message = "Pattern.searchReview.timePlayedFilter")
    @QueryParam("timeplayed")
    private String timePlayedFilter;

    @QueryParam("authorPreferences")
    private List<Integer> authorPreferences;

    @ExistentPlatformList
    @QueryParam("platforms")
    private List<String> platformsFilter;

    @ExistentDifficultyList
    @QueryParam("difficulty")
    private List<String> difficultyFilter;

    @QueryParam("completed")
    private Boolean completedFilter;

    @QueryParam("replayable")
    private Boolean replayableFilter;

    @QueryParam("search")
    private String search;

    @QueryParam("gameId")
    private Long gameId;

    @QueryParam("recommendedFor")
    private Long recommendedFor;

    @QueryParam("fromFollowing")
    private Long fromFollowing;

    @QueryParam("newForUser")
    private Long newForUser;


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
                .withFromFollowing(fromFollowing)
                .withNewForUser(newForUser)
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
