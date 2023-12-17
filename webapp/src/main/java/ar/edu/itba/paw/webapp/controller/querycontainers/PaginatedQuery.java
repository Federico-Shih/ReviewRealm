package ar.edu.itba.paw.webapp.controller.querycontainers;

import ar.edu.itba.paw.dtos.Page;

import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class PaginatedQuery {

    @Min(value = 1, message = "Size.pagination")
    @QueryParam("page")
    @DefaultValue("1")
    private int page;

    @Min(value = 1, message = "Size.pagination")
    @QueryParam("pageSize")
    @DefaultValue("10")
    private int pageSize;

    public Page getPage() {
        return Page.with(page, pageSize);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
