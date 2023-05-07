package ar.edu.itba.paw.dtos.ordering;

public class Ordering<T extends OrderCriteria> {
    private final OrderDirection orderDirection;
    private final T orderCriteria;

    public Ordering(OrderDirection orderDirection, T orderCriteria) {
        this.orderDirection = orderDirection;
        this.orderCriteria = orderCriteria;
    }

    public OrderDirection getOrderDirection() {
        return orderDirection;
    }

    public T getOrderCriteria() {
        return orderCriteria;
    }

    public static <V extends OrderCriteria> Ordering<V> defaultOrder(V defaultOrderCriteria) {
        return new Ordering<>(OrderDirection.DESCENDING, defaultOrderCriteria);
    }
}
