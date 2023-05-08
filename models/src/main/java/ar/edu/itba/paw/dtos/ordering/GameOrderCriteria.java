package ar.edu.itba.paw.dtos.ordering;

import java.util.Objects;

public enum GameOrderCriteria implements OrderCriteria {
    PUBLISH_DATE(0,"order.criteria.game.publish.date", "publishDate"),
    NAME(1,"order.criteria.game.name", "name");

    final Integer value;
    final String localizedNameCode;
    final String altName;

    public Integer getValue() {
        return value;
    }

    public String getAltName() {
        return altName;
    }

    public String getLocalizedNameCode() {
        return localizedNameCode;
    }

    GameOrderCriteria(Integer value, String localizedNameCode, String altName) {
        this.value = value;
        this.localizedNameCode = localizedNameCode;
        this.altName = altName;
    }

    public static GameOrderCriteria fromValue(Integer value) {
        for (GameOrderCriteria orderCriteria : values()) {
            if (Objects.equals(orderCriteria.getValue(), value)){
                return orderCriteria;
            }
        }
        return null;
    }
}