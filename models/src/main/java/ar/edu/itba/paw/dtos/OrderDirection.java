package ar.edu.itba.paw.dtos;

import java.util.Objects;

public enum OrderDirection {
    DESCENDING(0,"Descendente"),
    ASCENDING(1,"Ascendente");

    final Integer value;
    final String name;

    OrderDirection(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name;
    }

    public static OrderDirection fromValue(Integer value) {
        for (OrderDirection orderDirection : values()) {
            if (Objects.equals(orderDirection.getValue(), value)){
                return orderDirection;
            }
        }
        return null;
    }
}
