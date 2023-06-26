package ar.edu.itba.paw.dtos.filtering;

import ar.edu.itba.paw.enums.ReportReason;

public class ReportFilter {

    private final Long reviewId;

    private final Long reporterId;

    private final ReportReason reason;
    private final Boolean resolved;

    private final Long moderatorId;

    private final Long reportedUserId;

    private final Boolean closed;


    public ReportFilter(Long reviewId, Long reporterId, ReportReason reason,Boolean resolved, Long moderatorId, Long reportedUserId, Boolean closed) {
        this.reviewId = reviewId;
        this.reporterId = reporterId;
        this.reason = reason;
        this.resolved = resolved;
        this.moderatorId = moderatorId;
        this.reportedUserId = reportedUserId;
        this.closed = closed;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public ReportReason getReason() { return reason; }

    public Boolean getResolved() {
        return resolved;
    }

    public Long getModeratorId() {
        return moderatorId;
    }

    public Long getReportedUserId() {
        return reportedUserId;
    }

    public Boolean getClosed() {
        return closed;
    }
}


