package ar.edu.itba.paw.models;

import java.util.List;

public class Paginated<T> {
    private final int page;
    private final int pageSize;
    private final int totalPages;
    private final long totalElements;
    private final List<T> list;

    public Paginated(int page, int pageSize, int totalPages, long totalElements, List<T> list) {
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.list = list;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public List<T> getList() {
        return list;
    }
}
