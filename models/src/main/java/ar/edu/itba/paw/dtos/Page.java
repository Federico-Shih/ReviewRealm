package ar.edu.itba.paw.dtos;

public class Page {
    private int pageNumber = 0;
    private int pageSize = 10;

    public static Page with(int pageNumber, int pageSize) {
        Page page = new Page();
        page.pageNumber = pageNumber;
        page.pageSize = pageSize;
        return page;
    }
    private Page(){

    }

    public int getPageNumber() {
        return pageNumber;
    }

    public  int getOffset() {
        return (pageNumber - 1) * pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }
}
