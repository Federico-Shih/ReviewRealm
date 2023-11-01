package ar.edu.itba.paw.dtos.filtering;

import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.enums.ReportState;

public class ReportFilter {

    private final Long reviewId;

    private final Long reporterId;

    private final ReportReason reason;

    private final ReportState state;

    private final Long moderatorId;

    private final Long reportedUserId;


    public ReportFilter(Long reviewId, Long reporterId, ReportReason reason, ReportState state, Long moderatorId, Long reportedUserId) {
        this.reviewId = reviewId;
        this.reporterId = reporterId;
        this.reason = reason;
        this.state = state;
        this.moderatorId = moderatorId;
        this.reportedUserId = reportedUserId;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public ReportReason getReason() { return reason; }

    public ReportState getState() {
        return state;
    }

    public Long getModeratorId() {
        return moderatorId;
    }

    public Long getReportedUserId() {
        return reportedUserId;
    }

}


