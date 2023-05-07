package ar.edu.itba.paw.persistenceinterfaces;

public interface PaginationDao<T> {
    Long count(T filter);

    default int getPageCount(T filter, int pageSize) {
        return (int) Math.ceil((double) count(filter) / pageSize);
    }
}
