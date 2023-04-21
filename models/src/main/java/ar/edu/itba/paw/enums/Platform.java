package ar.edu.itba.paw.enums;

public enum Platform {
    PC("platform.pc"), XBOX("platform.xbox"), PS("platform.ps"), NINTENDO("platform.nintendo");

    private final String code;

    Platform(String code) {
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
