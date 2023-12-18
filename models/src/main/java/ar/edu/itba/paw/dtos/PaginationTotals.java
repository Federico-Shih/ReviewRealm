package ar.edu.itba.paw.dtos;

public class PaginationTotals {
    private final int totalPages;
    private final long totalElements;

    public PaginationTotals(int totalPages, long totalElements) {
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }
}
