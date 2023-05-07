package ar.edu.itba.paw.dtos.ordering;

import java.util.Objects;

public enum OrderDirection {
    DESCENDING(0,"order.direction.desc", "DESC"),
    ASCENDING(1,"order.direction.asc", "ASC");

    final Integer value;
    final String localizedNameCode;
    final String altName;

    OrderDirection(int value, String localizedNameCode, String altName) {
        this.value = value;
        this.localizedNameCode = localizedNameCode;
        this.altName = altName;
    }

    public Integer getValue() {
        return value;
    }

    public String getLocalizedNameCode() {
        return localizedNameCode;
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
