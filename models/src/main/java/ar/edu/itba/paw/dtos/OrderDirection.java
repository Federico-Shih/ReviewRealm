package ar.edu.itba.paw.dtos;

import java.util.Objects;

public enum OrderDirection {
    DESCENDING(0,"Descendente", "DESC"),
    ASCENDING(1,"Ascendente", "ASC");

    final Integer value;
    final String name;
    final String altName;

    OrderDirection(int value, String name, String altName) {
        this.value = value;
        this.name = name;
        this.altName = altName;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getAltName() {
        return this.altName;
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
