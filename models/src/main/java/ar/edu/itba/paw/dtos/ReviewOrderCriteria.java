package ar.edu.itba.paw.dtos;

import java.util.Objects;

public enum ReviewOrderCriteria {
    REVIEW_DATE(0, "Fecha"),
    REVIEW_SCORE(1, "Puntaje");

    final Integer value;
    final String name;

    ReviewOrderCriteria(Integer value, String name) {
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

    public static ReviewOrderCriteria fromValue(Integer value) {
        for (ReviewOrderCriteria orderCriteria : values()) {
            if (Objects.equals(orderCriteria.getValue(), value)){
                return orderCriteria;
            }
        }
        return null;
    }
}
