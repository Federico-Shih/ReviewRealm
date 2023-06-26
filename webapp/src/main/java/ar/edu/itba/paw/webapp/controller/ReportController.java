package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.ReportService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ReportController {

    private final ReportService reportService;

    private final UserService userService;
    private static final int PAGE_SIZE = 8;
    private static final int INITIAL_PAGE = 1;
    private static final int MAX_SEARCH_RESULTS = 6;

    @Autowired
    public ReportController(ReportService reportService, UserService userService) {
        this.reportService = reportService;
        this.userService = userService;
    }
    @RequestMapping(value = "/report/review/{id:\\d+}", method = RequestMethod.POST)
    ModelAndView reviewReportSubmit(@PathVariable(value = "id") Long reviewId,
                                    @RequestParam(value = "reason") String reason) {
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
        ReportReason rs;
        try {
            rs = ReportReason.valueOf(reason);
        } catch (IllegalArgumentException e) {
            return new ModelAndView("redirect:/review/" + reviewId);
        }
        reportService.createReport(loggedUser.getId(), reviewId, rs);

        return new ModelAndView("redirect:/review/" + reviewId + "?reported=true");
    }

}
