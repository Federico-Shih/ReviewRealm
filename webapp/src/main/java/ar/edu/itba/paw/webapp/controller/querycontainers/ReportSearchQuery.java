package ar.edu.itba.paw.webapp.controller.querycontainers;

import ar.edu.itba.paw.dtos.filtering.ReportFilter;
import ar.edu.itba.paw.dtos.filtering.ReportFilterBuilder;
import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.enums.ReportState;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentReviewId;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentUserId;

import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;

public class ReportSearchQuery extends PaginatedQuery {

    @QueryParam("reviewId")
    @ExistentReviewId(optional = true)
    private Long reviewId;

    @QueryParam("reporterId")
    @ExistentUserId(optional = true)
    private Long reporterId;

    @Pattern(regexp ="^(disrespectful|spam|irrelevant|spoiler|piracy|privacy)$"
            , flags = Pattern.Flag.CASE_INSENSITIVE, message = "Pattern.searchReport.reason")
    @QueryParam("reason")
    private String reason;

    @QueryParam("moderatorId")
    @ExistentUserId(optional = true)
    private Long moderatorId;

    @QueryParam("reportedUserId")
    @ExistentUserId(optional = true)
    private Long reportedUserId;

    public ReportSearchQuery() {
    }

    public ReportFilter getFilter() throws IllegalArgumentException {
        ReportReason reportReasonEnum = null;
        if(reason != null) {
            reportReasonEnum = ReportReason.valueOf(reason.toUpperCase());
        }
        return new ReportFilterBuilder()
                .withReviewId(reviewId)
                .withReporterId(reporterId)
                .withReason(reportReasonEnum)
                .withState(ReportState.UNRESOLVED)
                .withModeratorId(moderatorId)
                .withReportedUserId(reportedUserId)
                .build();
    }

}
