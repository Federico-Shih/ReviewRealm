package ar.edu.itba.paw.enums;

public enum ReportReason {
    DISRESPECTFUL("community.rules.options.respect"),
    SPAM("community.rules.options.spam"),
    IRRELEVANT("community.rules.options.relevancy"),
    SPOILER("community.rules.options.spoilers"),
    PIRACY("community.rules.options.piracy"),
    PRIVACY("community.rules.options.privacy");

    private final String code;

    ReportReason(String code) {
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
