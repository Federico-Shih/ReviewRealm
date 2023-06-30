package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.ReportFilter;
import ar.edu.itba.paw.dtos.filtering.ReportFilterBuilder;
import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Pair;
import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.ReportService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import ar.edu.itba.paw.webapp.controller.helpers.PaginationHelper;
import ar.edu.itba.paw.webapp.controller.helpers.QueryHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
@Controller
public class ReportController {

    private final ReportService reportService;

    private final UserService userService;
    private static final int PAGE_SIZE = 8;
    private static final int INITIAL_PAGE = 1;


    @Autowired
    public ReportController(ReportService reportService, UserService userService) {
        this.reportService = reportService;
        this.userService = userService;
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
