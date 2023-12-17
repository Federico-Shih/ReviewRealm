package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.exceptions.ReportAlreadyExistsException;
import ar.edu.itba.paw.exceptions.ReportAlreadyResolvedException;
import ar.edu.itba.paw.exceptions.ReviewNotFoundException;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.ReportService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import ar.edu.itba.paw.webapp.controller.forms.AcceptRejectReportForm;
import ar.edu.itba.paw.webapp.controller.forms.SubmitReportForm;
import ar.edu.itba.paw.webapp.controller.mediatypes.VndType;
import ar.edu.itba.paw.webapp.controller.querycontainers.ReportSearchQuery;
import ar.edu.itba.paw.webapp.controller.responses.PaginatedResponseHelper;
import ar.edu.itba.paw.webapp.controller.responses.ReportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("reports")
@Component
public class ReportController {

    private final ReportService reportService;

    private final UserService userService;

    @Context
    private UriInfo uriInfo;


    @Autowired
    public ReportController(ReportService reportService, UserService userService) {
        this.reportService = reportService;
        this.userService = userService;
    }

    @GET
    @Path("{id:\\d+}")
    @Produces(VndType.APPLICATION_REPORT)
    public Response getById(@PathParam("id") final long id) {
        final Optional<Report> report = reportService.getReportById(id);
        if(!report.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(ReportResponse.fromEntity(uriInfo, report.get())).build();
    }

    @GET
    @Produces(VndType.APPLICATION_REPORT_LIST)
    public Response getReports(@Valid @BeanParam ReportSearchQuery reportSearchQuery) {
        final Paginated<Report> reports = reportService.getReports(reportSearchQuery.getPage(), reportSearchQuery.getFilter());
        if (reports.getTotalPages() == 0 || reports.getList().isEmpty()) {
            return Response.noContent().build();
        }
        List<ReportResponse> reportResponseList = reports.getList().stream().map(report -> ReportResponse.fromEntity(uriInfo,report)).collect(Collectors.toList());
        return PaginatedResponseHelper.fromPaginated(uriInfo, reportResponseList, reports).build();
    }

    @POST
    @Consumes(VndType.APPLICATION_REPORT_FORM)
    @Produces(VndType.APPLICATION_REPORT)
    public Response submitReport(@Valid @NotNull(message = "error.body.empty") SubmitReportForm reportForm) throws ReportAlreadyExistsException, ReviewNotFoundException {
        long reporterId = AuthenticationHelper.getLoggedUser(userService).getId();
        ReportReason rs = ReportReason.valueOf(reportForm.getReason().toUpperCase());
        final Report report;
        User loggedIn = AuthenticationHelper.getLoggedUser(userService);
        report = reportService.createReport(reporterId, reportForm.getReviewId(), rs);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path("/reports").path(report.getId().toString()).build())
                .entity(ReportResponse.ReportResponseBuilder
                        .fromReport(report, uriInfo)
                        .withAuthed(loggedIn)
                        .build())
                .build();
    }

    @PATCH
    @Path("{id:\\d+}")
    @Consumes(VndType.APPLICATION_REPORT_HANDLE_FORM)
    @Produces(VndType.APPLICATION_REPORT)
    public Response acceptRejectReport(@PathParam("id") final long id, @Valid @NotNull(message = "error.body.empty") AcceptRejectReportForm reportForm) throws ReportAlreadyResolvedException {
        long userId = AuthenticationHelper.getLoggedUser(userService).getId();
        Report report;
        if (reportForm.getState().equals("accepted")) {
            report = reportService.resolveReport(id, userId);
        } else {
            report = reportService.rejectReport(id, userId);
        }
        return Response.ok(ReportResponse.fromEntity(uriInfo, report)).build();
    }
}
