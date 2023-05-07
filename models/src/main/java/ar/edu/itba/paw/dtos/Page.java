package ar.edu.itba.paw.dtos;

public class Page {
    Integer pageNumber = 0;
    Integer pageSize = 10;

    public static Page with(Integer pageNumber, Integer pageSize) {
        Page page = new Page();
        page.pageNumber = pageNumber;
        page.pageSize = pageSize;
        return page;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public Long getOffset() {
        return (long) (pageNumber - 1) * pageSize;
    }

    public Integer getPageSize() {
        return pageSize;
    }
}
