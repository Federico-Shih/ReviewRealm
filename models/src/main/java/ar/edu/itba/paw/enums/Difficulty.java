package ar.edu.itba.paw.enums;

import java.util.Optional;

public enum Difficulty {
    HARD(0, "difficulty.hard"), MEDIUM(1, "difficulty.medium"), EASY(2, "difficulty.easy");

    private final int id;
    private final String code;
    Difficulty(int id, String code) {
        this.id = id;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return super.name();
    }

    public static Optional<Difficulty> getById(int id) {
        for (Difficulty difficulty : values()) {
            if (difficulty.id == id) {
                return Optional.of(difficulty);
            }
        }
        return Optional.empty();
    }
}

