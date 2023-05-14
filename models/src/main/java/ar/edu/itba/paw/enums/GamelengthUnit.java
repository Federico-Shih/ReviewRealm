package ar.edu.itba.paw.enums;

public enum GamelengthUnit {
    HOURS(60 * 60, "unit.hours"), MINUTES(60, "unit.minutes");
    private final int rate;
    private final String code;

    GamelengthUnit(int rate, String code) {
        this.rate = rate;
        this.code = code;
    }

    public Double toSeconds(Double number) {
        return number * rate;
    }

    public String getCode() {
        return code;
    }
}
