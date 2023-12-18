package ar.edu.itba.paw.dtos.ordering;

import java.util.Objects;

public enum OrderDirection {
    DESCENDING("order.direction.desc", "DESC"),
    ASCENDING("order.direction.asc", "ASC");

    final String localizedNameCode;
    final String altName;

    OrderDirection(String localizedNameCode, String altName) {
        this.localizedNameCode = localizedNameCode;
        this.altName = altName;
    }

    public String getLocalizedNameCode() {
        return localizedNameCode;
    }

    public String getAltName() {
        return this.altName;
    }

    public static OrderDirection fromString(String value) {
        for (OrderDirection orderDirection : values()) {
            if (Objects.equals(orderDirection.getAltName(), value.toUpperCase())){
                return orderDirection;
            }
        }
        return null;
    }
}
