package ar.edu.itba.paw.dtos.ordering;

import java.util.Objects;

public enum UserOrderCriteria implements OrderCriteria {

    LEVEL(0, "order.criteria.user.level", "xp", "xp"),
    FOLLOWERS(1, "order.criteria.user.followers", "followers.size", "follower_count"),
    REPUTATION(2, "order.criteria.user.reputation", "reputation", "reputation");

    final Integer value;
    final String localizedNameCode;
    final String altName;

    final String tableName;

    public Integer getValue() {
        return value;
    }

    public String getAltName() {
        return altName;
    }

    public String getLocalizedNameCode() {
        return localizedNameCode;
    }

    public String getTableName() {
        return tableName;
    }

    UserOrderCriteria(Integer value, String localizedNameCode, String altName, String tableName) {
        this.value = value;
        this.localizedNameCode = localizedNameCode;
        this.altName = altName;
        this.tableName = tableName;
    }

    public static UserOrderCriteria fromValue(Integer value) {
        for (UserOrderCriteria orderCriteria : values()) {
            if (Objects.equals(orderCriteria.getValue(), value)){
                return orderCriteria;
            }
        }
        return null;
    }
}
