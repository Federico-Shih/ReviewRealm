package ar.edu.itba.paw.dtos.ordering;

import java.util.Objects;

public enum ReviewOrderCriteria implements OrderCriteria {
    REVIEW_DATE(0, "order.criteria.review.date", "created", "createddate"),
    REVIEW_SCORE(1, "order.criteria.review.score", "rating", "rating"),
    REVIEW_POPULAR(2, "order.criteria.review.popular", "popularity", "popularity"),
    REVIEW_CONTROVERSIAL(3, "order.criteria.review.controversial", "controversial", "controversial");

    private final Integer value;
    private final String localizedNameCode;
    private final String altName;
    private final String tableName;

    ReviewOrderCriteria(Integer value, String localizedNameCode, String altName, String tableName) {
        this.value = value;
        this.localizedNameCode = localizedNameCode;
        this.altName = altName;
        this.tableName = tableName;
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

    public String getTableName() {
        return tableName;
    }
}
