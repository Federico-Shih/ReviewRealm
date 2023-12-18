package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.dtos.PaginationTotals;

public interface PaginationDao<T> {
    long count(T filter);

    default PaginationTotals getPaginationTotals(T filter, int pageSize) {
        if (pageSize <= 0)
            throw new IllegalArgumentException("Page size must be positive");
        long count = count(filter);
        return new PaginationTotals((int) Math.ceil((double) count / pageSize), count);
    }
}
