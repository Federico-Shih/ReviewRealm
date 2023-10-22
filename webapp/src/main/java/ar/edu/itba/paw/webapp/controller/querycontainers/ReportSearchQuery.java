package ar.edu.itba.paw.webapp.controller.querycontainers;

import ar.edu.itba.paw.dtos.filtering.ReportFilter;
import ar.edu.itba.paw.dtos.filtering.ReportFilterBuilder;
import ar.edu.itba.paw.enums.ReportReason;

import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;

public class ReportSearchQuery extends PaginatedQuery {

    @QueryParam("reviewId")
    private Long reviewId;

    @QueryParam("reporterId")
    private Long reporterId;

    @Pattern(regexp ="^(disrespectful|spam|irrelevant|spoiler|piracy|privacy)$", flags = Pattern.Flag.CASE_INSENSITIVE)
    @QueryParam("reportReason")
    private String reportReason;

    @QueryParam("moderatorId")
    private Long moderatorId;

    @QueryParam("reportedUserId")
    private Long reportedUserId;

    public ReportFilter getFilter() throws IllegalArgumentException {
        ReportReason reportReasonEnum = null;
        if(reportReason != null) {
            reportReasonEnum = ReportReason.valueOf(reportReason.toUpperCase());
        }
        return new ReportFilterBuilder()
                .withReviewId(reviewId)
                .withReporterId(reporterId)
                .withReason(reportReasonEnum)
                .withResolved(false)
                .withModeratorId(moderatorId)
                .withReportedUserId(reportedUserId)
                .withClosed(false)
                .build();
    }

}
