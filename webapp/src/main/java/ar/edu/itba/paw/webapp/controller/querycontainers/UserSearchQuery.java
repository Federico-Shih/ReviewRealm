package ar.edu.itba.paw.webapp.controller.querycontainers;

/*
//            @RequestParam(value = "search", defaultValue = "") final String search,
//            @RequestParam(value = "page", defaultValue = "1") Integer page,
//            @RequestParam(value = "pageSize", required = false) Integer pageSize,
//            @RequestParam(value = "o-crit", defaultValue = "0") Integer orderCriteria,
//            @RequestParam(value = "o-dir", defaultValue = "0") Integer orderDirection
 */

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.UserFilter;
import ar.edu.itba.paw.dtos.filtering.UserFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.UserOrderCriteria;
import org.springframework.security.access.method.P;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;
import java.util.List;

public class UserSearchQuery extends PaginatedQuery {

    @QueryParam("search")
    private String search;

    @Pattern(regexp ="^(level|followers|reputation)$", flags = Pattern.Flag.CASE_INSENSITIVE)
    @QueryParam("sort")
    private String orderCriteria;

    @Pattern(regexp ="^(asc|desc)$", flags = Pattern.Flag.CASE_INSENSITIVE)
    @QueryParam("direction")
    private String orderDirection;

    @QueryParam("username")
    private String username;

    @QueryParam("email")
    private String email;

    @QueryParam("id")
    private Long id;

    @QueryParam("preferences")
    private List<Integer> preferences;

    @QueryParam("gamesPlayed")
    private List<Long> gamesPlayed;

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
