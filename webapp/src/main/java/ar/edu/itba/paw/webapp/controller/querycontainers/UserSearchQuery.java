package ar.edu.itba.paw.webapp.controller.querycontainers;

import ar.edu.itba.paw.dtos.filtering.UserFilter;
import ar.edu.itba.paw.dtos.filtering.UserFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.UserOrderCriteria;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentGenreList;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentUserId;

import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;
import java.util.List;

public class UserSearchQuery extends PaginatedQuery {

    @QueryParam("search")
    private String search;

    @Pattern(regexp ="^(level|followers|reputation)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Pattern.searchUsers.sort")
    @QueryParam("sort")
    private String orderCriteria;

    @Pattern(regexp ="^(asc|desc)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Pattern.search.direction")
    @QueryParam("direction")
    private String orderDirection;

    @QueryParam("username")
    private String username;

    @QueryParam("email")
    private String email;

    @QueryParam("id")
    private Long id;

    @ExistentGenreList
    @QueryParam("preferences")
    private List<Integer> preferences;

    @QueryParam("gamesPlayed")
    private List<Long> gamesPlayed;

    @ExistentUserId(optional = true)
    @QueryParam("following")
    private Long following;

    @ExistentUserId(optional = true)
    @QueryParam("followers")
    private Long followers;

    @ExistentUserId(optional = true)
    @QueryParam("samePreferencesAs")
    private Long samePreferencesAs;

    @ExistentUserId(optional = true)
    @QueryParam("sameGamesPlayedAs")
    private Long sameGamesPlayedAs;

    public UserSearchQuery() {
    }

    public String getSearch() {
        return search;
    }

    public UserFilter getFilter() {
        return new UserFilterBuilder()
                .withSearch(search)
                .withEmail(email)
                .withGamesPlayed(gamesPlayed)
                .withPreferences(preferences)
                .withId(id)
                .withUsername(username)
                .withFollowers(followers)
                .withFollowing(following)
                .withSamePreferencesAs(samePreferencesAs)
                .withSameGamesPlayedAs(sameGamesPlayedAs)
                .build();
    }

    public Ordering<UserOrderCriteria> getOrdering() {
        OrderDirection direction = OrderDirection.fromString(orderDirection != null ? orderDirection : "");
        UserOrderCriteria criteria = UserOrderCriteria.fromString(orderCriteria != null ? orderCriteria : "");
        return new Ordering<>(
                direction != null ? direction : OrderDirection.DESCENDING,
                criteria != null ? criteria : UserOrderCriteria.REPUTATION
            );
    }
}
