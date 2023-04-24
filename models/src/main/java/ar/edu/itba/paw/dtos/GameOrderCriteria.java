package ar.edu.itba.paw.dtos;

import java.util.Objects;

public enum GameOrderCriteria {
    PUBLISH_DATE(0,"order.criteria.game.publish.date"),
    NAME(1,"order.criteria.game.name");

    final Integer value;
    final String localizedNameCode;

    public Integer getValue() {
        return value;
    }

    public String getLocalizedNameCode() {
        return localizedNameCode;
    }

    GameOrderCriteria(Integer value, String localizedNameCode) {
        this.value = value;
        this.localizedNameCode = localizedNameCode;
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
