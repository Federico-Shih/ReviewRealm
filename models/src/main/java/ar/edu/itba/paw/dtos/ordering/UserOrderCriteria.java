package ar.edu.itba.paw.dtos.ordering;

import java.util.Objects;

public enum UserOrderCriteria implements OrderCriteria {

    LEVEL("order.criteria.user.level", "xp", "xp"),
    FOLLOWERS("order.criteria.user.followers", "followers.size", "follower_count"),
    REPUTATION( "order.criteria.user.reputation", "reputation", "reputation");

    final String localizedNameCode;
    final String altName;

    final String tableName;


    public String getAltName() {
        return altName;
    }

    public String getLocalizedNameCode() {
        return localizedNameCode;
    }

    public String getTableName() {
        return tableName;
    }

    UserOrderCriteria(String localizedNameCode, String altName, String tableName) {
        this.localizedNameCode = localizedNameCode;
        this.altName = altName;
        this.tableName = tableName;
    }


    public static UserOrderCriteria fromString(String string) {
        for (UserOrderCriteria orderCriteria : values()) {
            if (orderCriteria.name().equals(string.toUpperCase())){
                return orderCriteria;
            }
        }
        return null;
    }
}
