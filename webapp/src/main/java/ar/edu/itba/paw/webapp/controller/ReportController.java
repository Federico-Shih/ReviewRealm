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
import ar.edu.itba.paw.webapp.controller.querycontainers.ReportSearchQuery;
import ar.edu.itba.paw.webapp.controller.responses.PaginatedResponse;
import ar.edu.itba.paw.webapp.controller.responses.ReportResponse;
import ar.edu.itba.paw.webapp.controller.forms.SubmitReportForm;
import ar.edu.itba.paw.webapp.exceptions.CustomRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/reports")
@Component
public class ReportController extends UriInfoController {

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
    @Path("{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getById(@PathParam("id") final long id) {
        final Optional<Report> report = reportService.getReportById(id);
        User loggedIn = AuthenticationHelper.getLoggedUser(userService);
        if(!report.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(ReportResponse.fromEntity(uriInfo, report.get())).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReports(@Valid @BeanParam ReportSearchQuery reportSearchQuery) {
        final Paginated<Report> reports = reportService.getReports(reportSearchQuery.getPage(), reportSearchQuery.getFilter());
        if (reports.getTotalPages() == 0 || reports.getList().isEmpty()) {
            return Response.noContent().build();
        }
        List<ReportResponse> reportResponseList = reports.getList().stream().map(this.currifyUriInfo(ReportResponse::fromEntity)).collect(Collectors.toList());
        return Response.ok(PaginatedResponse.fromPaginated(uriInfo, reportResponseList, reports)).build();
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response submitReport(@Valid SubmitReportForm reportForm) throws ReportAlreadyExistsException, ReviewNotFoundException {
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
    @Path("{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response acceptRejectReport(@PathParam("id") final long id, @Valid AcceptRejectReportForm reportForm) throws ReportAlreadyResolvedException {
        long userId = AuthenticationHelper.getLoggedUser(userService).getId();
        Report report;
        if (reportForm.getState().equals("accepted")) {
            report = reportService.resolveReport(id, userId);
        } else {
            report = reportService.rejectReport(id, userId);
        }
        return Response.ok(ReportResponse.fromEntity(uriInfo, report)).build();
    }


//    @RequestMapping(value = "/report/review/{id:\\d+}", method = RequestMethod.POST)
//    ModelAndView reviewReportSubmit(@PathVariable(value = "id") Long reviewId,
//                                    @RequestParam(value = "reason") String reason) {
//        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
//        ReportReason rs;
//        try {
//            rs = ReportReason.valueOf(reason);
//        } catch (IllegalArgumentException e) {
//            return new ModelAndView("redirect:/review/" + reviewId);
//        }
//        reportService.createReport(loggedUser.getId(), reviewId, rs);
//
//        return new ModelAndView("redirect:/review/" + reviewId + "?reported=true");
//    }
//
//    @RequestMapping(value = "/report/reviews", method = RequestMethod.GET)
//    ModelAndView viewReports(@RequestParam(value = "page", defaultValue = "1") Integer page,
//                             @RequestParam(value = "pagesize", defaultValue = "8") Integer pageSize) {
//        ModelAndView mav = new ModelAndView("/reports/reports-view");
//        ReportFilter filter = new ReportFilterBuilder().withResolved(false).withClosed(false).build();
//        if(pageSize == null || pageSize < 1) {
//            pageSize = PAGE_SIZE;
//        }
//        if(page<=0)
//            page = INITIAL_PAGE;
//        List<Pair<String, Object>> queriesToKeepAtPageChange = new ArrayList<>();
//        queriesToKeepAtPageChange.add(new Pair<>("pagesize", pageSize));
//        mav.addObject("queriesToKeepAtPageChange", QueryHelper.toQueryString(queriesToKeepAtPageChange));
//
//        Paginated<Report> reports = reportService.getReports(Page.with(page, pageSize),filter);
//        PaginationHelper.paginate(mav, reports);
//        mav.addObject("reports", reports.getList());
//        mav.addObject("pageSize", pageSize);
//        return mav;
//    }
//
//    @RequestMapping(value = "/report/reviews/{id:\\d+}/resolve", method = RequestMethod.POST)
//    ModelAndView resolveReport(@PathVariable(value = "id") Long reportId, @RequestParam(value = "page") Integer page,
//                               @RequestParam(value = "pagesize") Integer pageSize) {
//        reportService.resolveReport(reportId,AuthenticationHelper.getLoggedUser(userService).getId());
//        return new ModelAndView("redirect:/report/reviews" + "?page="+page+"&pagesize="+pageSize);
//    }
//
//    @RequestMapping(value = "/report/reviews/{id:\\d+}/reject", method = RequestMethod.POST)
//    ModelAndView rejectReport(@PathVariable(value = "id") Long reportId,  @RequestParam(value = "page") Integer page,
//                              @RequestParam(value = "pagesize") Integer pageSize) {
//        reportService.rejectReport(reportId,AuthenticationHelper.getLoggedUser(userService).getId());
//        return new ModelAndView("redirect:/report/reviews" + "?page="+page+"&pagesize="+pageSize);
//    }

}
