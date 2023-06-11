package ar.edu.itba.paw.enums;

public enum LevelRange {
    BASIC(0,9, "basic"),
    BRONZE(10,19, "bronze"),
    SILVER(20,29, "silver"),
    GOLD(30,39, "gold"),
    EPIC(40,-1, "epic");
    private final int min;
    private final int max;
    private final String rangeTitle;

    LevelRange (int min, int max, String rangeTitle) {
        this.min = min;
        this.max = max;
        this.rangeTitle = rangeTitle;
    }

    public String getRangeTitle() {
        return rangeTitle;
    }

    public static LevelRange levelToRange(int level) {
        for (LevelRange range: values()) {
            if(level >= range.min && (level <= range.max || range.max < 0))
                return range;
        }
        return BASIC;
    }
}
