package ar.edu.itba.paw.persistence.helpers;

import ar.edu.itba.paw.dtos.ordering.OrderCriteria;
import ar.edu.itba.paw.dtos.ordering.Ordering;

import javax.persistence.Query;
import java.util.ArrayList;

public class DaoUtils {
    private DaoUtils() {
        // Utility class
    }

    public static <T extends OrderCriteria>String toOrderString(Ordering<T> order, boolean isNative) {
        if (order == null || order.getOrderCriteria() == null) {
            return "";
        }
        StringBuilder orderQuery = new StringBuilder();
        orderQuery.append(" ORDER BY ");
        orderQuery.append(isNative ? order.getOrderCriteria().getTableName() : order.getOrderCriteria().getAltName());
        if (order.getOrderDirection() != null) {
            orderQuery.append(" ");
            orderQuery.append(order.getOrderDirection().getAltName());
            orderQuery.append(" ");
            orderQuery.append("NULLS LAST");
        }
        return orderQuery.toString();
    }

    public static void setNativeParameters(QueryBuilder queryBuilder, Query nativeQuery) {
        int length = queryBuilder.toArguments().size();
        ArrayList<Object> array = new ArrayList<>(queryBuilder.toArguments());
        for (int i = 0; i < length; i += 1) {
            nativeQuery.setParameter(i + 1, array.get(i));
        }
    }
}
