package ar.edu.itba.paw.dtos.ordering;

import java.util.Objects;

public enum ReviewOrderCriteria implements OrderCriteria {
    REVIEW_DATE("order.criteria.review.date", "created", "createddate"),
    REVIEW_SCORE("order.criteria.review.score", "rating", "rating"),
    REVIEW_POPULAR("order.criteria.review.popular", "popularity", "popularity"),
    REVIEW_CONTROVERSIAL("order.criteria.review.controversial", "controversial", "controversial");

    private final String localizedNameCode;
    private final String altName;
    private final String tableName;

    ReviewOrderCriteria(String localizedNameCode, String altName, String tableName) {
        this.localizedNameCode = localizedNameCode;
        this.altName = altName;
        this.tableName = tableName;
    }

    public String getLocalizedNameCode() {
        return localizedNameCode;
    }

    public String getAltName() {
        return this.altName;
    }


    public static ReviewOrderCriteria fromString(String string) {
        for (ReviewOrderCriteria orderCriteria : values()) {
            if (orderCriteria.altName.equalsIgnoreCase(string)) {
                return orderCriteria;
            }
        }
        return null;
    }

    public String getTableName() {
        return tableName;
    }
}
