package ar.edu.itba.paw.models.keys;

import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;

import java.io.Serializable;
import java.util.Objects;

public class ReportId implements Serializable {

    private User reporter;

    private Review reportedReview;


    public ReportId(User reporter, Review review) {
        this.reporter = reporter;
        this.reportedReview = review;
    }
    protected ReportId() {
        // For hibernate
    }

    public User getReporter() {
        return reporter;
    }

    public Review getReportedReview() {
        return reportedReview;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportId reportId = (ReportId) o;
        return Objects.equals(reporter, reportId.reporter) && Objects.equals(reportedReview, reportId.reportedReview);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reporter, reportedReview);
    }
}
