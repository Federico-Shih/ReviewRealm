package ar.edu.itba.paw.dtos.ordering;

public enum GameOrderCriteria implements OrderCriteria {
    PUBLISH_DATE("order.criteria.game.publish.date", "publishDate"),
    NAME("order.criteria.game.name", "name"),
    AVERAGE_RATING("game.details.review.statistics.rating", "averageRating");

    final String localizedNameCode;
    final String altName;

    @Override
    public String getAltName() {
        return altName;
    }

    @Override
    public String getTableName() {
        return altName;
    }

    public String getLocalizedNameCode() {
        return localizedNameCode;
    }

    GameOrderCriteria(String localizedNameCode, String altName) {
        this.localizedNameCode = localizedNameCode;
        this.altName = altName;
    }

    public static GameOrderCriteria fromString(String string) {
        for (GameOrderCriteria orderCriteria : values()) {
            if (orderCriteria.altName.equalsIgnoreCase(string)) {
                return orderCriteria;
            }
        }
        return null;
    }
}
