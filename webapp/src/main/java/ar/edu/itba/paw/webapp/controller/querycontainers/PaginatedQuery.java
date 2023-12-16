package ar.edu.itba.paw.webapp.controller.querycontainers;

import ar.edu.itba.paw.dtos.Page;

import javax.validation.constraints.Min;
import javax.ws.rs.QueryParam;

public class PaginatedQuery {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE = 1;
    @Min(value = 1, message = "Size.pagination")
    @QueryParam("page")
    private Integer page;

    @Min(value = 1, message = "Size.pagination")
    @QueryParam("pageSize")
    private Integer pageSize;

    public Page getPage() {
        return Page.with(page != null ? page : DEFAULT_PAGE , pageSize != null ? pageSize : DEFAULT_PAGE_SIZE);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
