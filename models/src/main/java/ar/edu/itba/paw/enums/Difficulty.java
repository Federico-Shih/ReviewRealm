package ar.edu.itba.paw.enums;

public enum Difficulty {
    HARD("difficulty.hard"), MEDIUM("difficulty.medium"), EASY("difficulty.easy");
    private final String code;
    Difficulty(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return super.name();
    }
}

