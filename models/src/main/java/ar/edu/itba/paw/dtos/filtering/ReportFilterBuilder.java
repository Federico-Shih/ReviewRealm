package ar.edu.itba.paw.dtos.filtering;

import ar.edu.itba.paw.enums.ReportReason;

import java.time.LocalDateTime;

public class ReportFilterBuilder {

    private Long reviewId = null;

    private Long reporterId = null;

    private ReportReason reason = null;

    private LocalDateTime from = null;

    private LocalDateTime to = null;

    private Long reportedUserId = null;

    private Boolean resolved = null;

    private Long moderatorId = null;

    private Boolean closed = null;

    public ReportFilterBuilder withReviewId(Long reviewId) {
        this.reviewId = reviewId;
        return this;
    }

    public ReportFilterBuilder withReporterId(Long reporterId) {
        this.reporterId = reporterId;
        return this;
    }

    public ReportFilterBuilder withReason(ReportReason reason) {
        this.reason = reason;
        return this;
    }

    public ReportFilterBuilder withResolved(Boolean resolved) {
        this.resolved = resolved;
        return this;
    }
    public ReportFilterBuilder withModeratorId(Long moderatorId) {
        this.moderatorId = moderatorId;
        return this;
    }
    public ReportFilterBuilder withReportedUserId(Long reportedUserId) {
        this.reportedUserId = reportedUserId;
        return this;
    }
    public ReportFilterBuilder withClosed(Boolean closed) {
        this.closed = closed;
        return this;
    }


    public ReportFilter build() {
        return new ReportFilter(reviewId, reporterId, reason, resolved, moderatorId, reportedUserId,closed);
    }
}
