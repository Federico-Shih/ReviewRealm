package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.UriInfo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportResponse extends BaseResponse{
    private static final String USERS_BASE_PATH = "/users";
    private static final String REVIEWS_BASE_PATH = "/reviews";
    private static final String REPORTS_BASE_PATH = "/reports";
    private long id;
    private ReportReason reason;
    private String submissionDate;
    private String state;

    private String resolvedDate;

    public static ReportResponse fromEntity(final UriInfo uri, Report report) {
        ReportResponse response = baseFromEntity(uri, report);
        if(!report.isResolved()){
            response.link("resolve", uri.getBaseUriBuilder().path(REPORTS_BASE_PATH).path(String.valueOf(report.getId())).build());
        }
        return response;
    }

    private static ReportResponse baseFromEntity(final UriInfo uri, Report report) {
        ReportResponse response = new ReportResponse();
        response.setId(report.getId());
        response.setReason(report.getReason());
        response.setState(report.getState().toString());
        response.setSubmissionDate(report.getSubmissionDate().format(DateTimeFormatter.ISO_DATE));
        response.setResolvedDate(report.getResolvedDate() != null? report.getResolvedDate().format(DateTimeFormatter.ISO_DATE) : null);
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

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getResolvedDate() {
        return resolvedDate;
    }

    public void setResolvedDate(String resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    public static class ReportResponseBuilder {
        private ReportResponse reportResponse;
        private Report report;
        private UriInfo uriInfo;

        protected ReportResponseBuilder() {
        }

        public static ReportResponseBuilder fromReport(Report report, UriInfo info) {
            ReportResponseBuilder builder = new ReportResponseBuilder();
            builder.reportResponse = ReportResponse.baseFromEntity(info, report);
            builder.report = report;
            builder.uriInfo = info;
            return builder;
        }

        public ReportResponseBuilder withAuthed(User currentUser) {
            if (currentUser == null) return this;
            if (currentUser.isModerator()) {
                if(!report.isResolved()){
                    reportResponse.link("resolve", uriInfo.getBaseUriBuilder().path(REPORTS_BASE_PATH).path(String.valueOf(report.getId())).build());
                }
            }
            return this;
        }

        public ReportResponse build() {
            return reportResponse;
        }
    }
}
