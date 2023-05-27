package ar.edu.itba.paw.dtos.ordering;

import java.util.Objects;

public enum ReviewOrderCriteria implements OrderCriteria {
    REVIEW_DATE(0, "order.criteria.review.date", "created"),
    REVIEW_SCORE(1, "order.criteria.review.score", "rating"),
    REVIEW_POPULAR(2, "order.criteria.review.popular", "popularity"),
    REVIEW_CONTROVERSIAL(3, "order.criteria.review.controversial", "controversial");

    final Integer value;
    final String localizedNameCode;
    final String altName;

    ReviewOrderCriteria(Integer value, String localizedNameCode, String altName) {
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

    public static ReviewOrderCriteria fromValue(Integer value) {
        for (ReviewOrderCriteria orderCriteria : values()) {
            if (Objects.equals(orderCriteria.getValue(), value)){
                return orderCriteria;
            }
        }
        return null;
    }
}
