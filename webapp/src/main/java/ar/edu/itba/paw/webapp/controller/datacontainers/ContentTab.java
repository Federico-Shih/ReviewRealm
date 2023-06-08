package ar.edu.itba.paw.webapp.controller.datacontainers;

public enum ContentTab {
    FOLLOWING("for-you.reviews.following.header", "for-you.reviews.following.notfound"),
    RECOMMENDED("for-you.reviews.recommended.header", "for-you.reviews.recommended.notfound"),
    NEW("for-you.reviews.new.header", "for-you.reviews.new.notfound");

    private final String headerCode;
    private final String notFoundCode;

    ContentTab(String headerCode, String notFoundCode) {
        this.headerCode = headerCode;
        this.notFoundCode = notFoundCode;
    }

    public String getHeaderCode() {
        return headerCode;
    }

    public String getNotFoundCode() { return notFoundCode; }

    @Override
    public String toString() {return super.name(); }
}

