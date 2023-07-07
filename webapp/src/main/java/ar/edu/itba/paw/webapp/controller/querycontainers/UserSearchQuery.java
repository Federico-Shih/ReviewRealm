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
import javax.ws.rs.QueryParam;

public class UserSearchQuery {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE = 1;

    @QueryParam("search")
    private String search;

    @Min(1)
    @QueryParam("page")
    private Integer page;

    @Min(1)
    @QueryParam("pageSize")
    private Integer pageSize ;

    @QueryParam("sort")
    private UserOrderCriteria orderCriteria = UserOrderCriteria.LEVEL;

    @QueryParam("direction")
    private OrderDirection orderDirection = OrderDirection.DESCENDING;

    public UserSearchQuery() {
    }

    public Page getPage() {
        return Page.with(page != null ? page : DEFAULT_PAGE , pageSize != null ? pageSize : DEFAULT_PAGE_SIZE);
    }

    public String getSearch() {
        return search;
    }

    public UserFilter getFilter() {
        return new UserFilterBuilder()
                .withSearch(search)
                .build();
    }

    public Ordering<UserOrderCriteria> getOrdering() {
        return new Ordering<>(orderDirection, orderCriteria);
    }
}
