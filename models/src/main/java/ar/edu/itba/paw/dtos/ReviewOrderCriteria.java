package ar.edu.itba.paw.dtos;

import java.util.Objects;

public enum ReviewOrderCriteria {
    REVIEW_DATE(0, "order.criteria.review.date"),
    REVIEW_SCORE(1, "order.criteria.review.score");

    final Integer value;
    final String localizedNameCode;

    ReviewOrderCriteria(Integer value, String localizedNameCode) {
        this.value = value;
        this.localizedNameCode = localizedNameCode;
    }

    public Integer getValue() {
        return value;
    }

    public String getLocalizedNameCode() {
        return localizedNameCode;
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
