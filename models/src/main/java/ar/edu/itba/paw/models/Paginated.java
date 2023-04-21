package ar.edu.itba.paw.models;

import java.util.List;

public class Paginated<T> {
    private int page;
    private int pageSize;
    private int totalPages;
    private List<T> list;

    public Paginated(int page, int pageSize, int totalPages, List<T> list) {
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
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

    public List<T> getList() {
        return list;
    }
}
