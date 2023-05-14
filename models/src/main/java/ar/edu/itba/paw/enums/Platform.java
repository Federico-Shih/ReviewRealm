package ar.edu.itba.paw.enums;

import java.util.Optional;

public enum Platform {
    PC(0, "platform.pc"), XBOX(1, "platform.xbox"), PS(2, "platform.ps"), NINTENDO(3, "platform.nintendo");

    private final Integer id;
    private final String code;

    Platform(Integer id, String code) {
        this.id = id;
        this.code = code;
    }

    public Integer getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return super.name();
    }

    public static Optional<Platform> getById(int id) {
        for (Platform platform : values()) {
            if (platform.id == id) {
                return Optional.of(platform);
            }
        }
        return Optional.empty();
    }
}
