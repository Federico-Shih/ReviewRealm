package ar.edu.itba.paw.persistenceinterfaces;

public interface PaginationDao<T> {
    Long count(T filter);

    default int getPageCount(T filter, int pageSize) {
        if (pageSize <= 0)
            throw new IllegalArgumentException("Page size must be positive");
        return (int) Math.ceil((double) count(filter) / pageSize);
    }
}
