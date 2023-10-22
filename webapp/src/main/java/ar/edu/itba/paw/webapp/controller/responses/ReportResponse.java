package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.models.Report;

import javax.ws.rs.core.UriInfo;
import java.time.LocalDateTime;

public class ReportResponse extends BaseResponse{
    private static String USERS_BASE_PATH = "/users";
    private static String REVIEWS_BASE_PATH = "/reviews";
    private static String REPORTS_BASE_PATH = "/reports";

    private long id;
    /*
    reporter
    reportedReview
    reportedUser
    moderator
     */
    private ReportReason reason;
    private LocalDateTime submissionDate;
    private String state;



    private LocalDateTime resolvedDate;

    public static ReportResponse fromEntity(final UriInfo uri, Report report) {
        ReportResponse response = new ReportResponse();
        response.setId(response.getId());
        response.setReason(report.getReason());
        if(!report.isResolved())
            response.setState("unresolved");
        else {
            if(report.getReportedReview() == null)
                response.setState("accepted");
            else
                response.setState("rejected");
        }
        response.setSubmissionDate(report.getSubmissionDate());
        response.setResolvedDate(report.getResolvedDate());
        response.link("self", uri.getBaseUriBuilder().path(REPORTS_BASE_PATH).path(String.valueOf(report.getId())).build());
        response.link("reporter", uri.getBaseUriBuilder().path(USERS_BASE_PATH).path(String.valueOf(report.getReporter().getId())).build());
        response.link("reportedUser", uri.getBaseUriBuilder().path(USERS_BASE_PATH).path(String.valueOf(report.getReportedUser().getId())).build());
        response.link("reportedReview", uri.getBaseUriBuilder().path(REVIEWS_BASE_PATH).path(String.valueOf(report.getReportedReview().getId())).build());
        if(report.getModerator() != null)
            response.link("moderator", uri.getBaseUriBuilder().path(USERS_BASE_PATH).path(String.valueOf(report.getModerator().getId())).build());

        return response;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ReportReason getReason() {
        return reason;
    }

    public void setReason(ReportReason reason) {
        this.reason = reason;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalDateTime getResolvedDate() {
        return resolvedDate;
    }

    public void setResolvedDate(LocalDateTime resolvedDate) {
        this.resolvedDate = resolvedDate;
    }
}