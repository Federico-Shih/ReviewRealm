package ar.edu.itba.paw.enums;

public enum FeedbackType {
    LIKE("feedback.like"), DISLIKE("feedback.dislike");

    private final String code;

    FeedbackType(String code) {
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
