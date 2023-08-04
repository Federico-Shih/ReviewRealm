package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.models.Paginated;

import javax.ws.rs.core.UriInfo;
import java.util.List;

public class PaginatedResponse<T, V extends BaseResponse> extends BaseResponse {
    private List<V> data;
    private long page;
    private long pageSize;
    private long totalPages;

    public static <T, V extends BaseResponse> PaginatedResponse<T, V> fromPaginated(UriInfo info, List<V> values, Paginated<T> pagination) {
        PaginatedResponse<T, V> response = new PaginatedResponse<>();
        response.data = values;
        response.page = pagination.getPage();
        response.pageSize = pagination.getPageSize();
        response.totalPages = pagination.getTotalPages();
        response.link("self", info.getRequestUri());
        if (pagination.getPage() > 1) {
            response.link("prev", info.getRequestUriBuilder().replaceQueryParam("page", pagination.getPage() - 1).build());
        }
        if (pagination.getPage() < pagination.getTotalPages()) {
            response.link("next", info.getRequestUriBuilder().replaceQueryParam("page", pagination.getPage() + 1).build());
        }
        return response;
    }

    public void setData(List<V> data) {
        this.data = data;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public List<V> getData() {
        return data;
    }

    public long getPage() {
        return page;
    }

    public long getPageSize() {
        return pageSize;
    }

    public long getTotalPages() {
        return totalPages;
    }
}
