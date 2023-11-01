package ar.edu.itba.paw.dtos.filtering;

import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.enums.ReportState;

import java.time.LocalDateTime;

public class ReportFilterBuilder {

    private Long reviewId = null;

    private Long reporterId = null;

    private ReportReason reason = null;

    private LocalDateTime from = null;

    private LocalDateTime to = null;

    private Long reportedUserId = null;

    private ReportState state = null;

    private Long moderatorId = null;


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

    public ReportFilterBuilder withState(ReportState state) {
        this.state = state;
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


    public ReportFilter build() {
        return new ReportFilter(reviewId, reporterId, reason, state, moderatorId, reportedUserId);
    }
}
