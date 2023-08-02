package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.models.Paginated;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

// TODO: ver si es necesario esto.
public class PaginatedResponse<T, V extends BaseResponse<V, T>> {
    private List<V> data;
    private long page;
    private long pageSize;
    private long totalPages;
    private URI prev;
    private URI next;
    private URI curr;

//    public PaginatedResponse<T, V> fromPaginated(UriInfo info, Paginated<T> paginated, Supplier<? extends V> ctor) {
//        this.data = paginated.getList().stream().map((value) -> ctor.get().fromEntity(info, value)).collect(Collectors.toList());
//        this.page = paginated.getPage();
//        this.pageSize = paginated.getPageSize();
//        this.totalPages = paginated.getTotalPages();
//        return this;
//    }

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
