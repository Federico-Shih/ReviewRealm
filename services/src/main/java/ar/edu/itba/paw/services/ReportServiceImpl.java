package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.ReportFilter;
import ar.edu.itba.paw.dtos.filtering.ReportFilterBuilder;
import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.enums.ReportState;
import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.ReportDao;
import ar.edu.itba.paw.servicesinterfaces.ReportService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportDao reportDao;

    private final UserService userService;

    private final ReviewService reviewService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Autowired
    public ReportServiceImpl(ReportDao reportDao, UserService userService, @Lazy ReviewService reviewService) {
        this.reportDao = reportDao;
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @Transactional
    @Override
    public Report createReport(long reporterId, long reviewToReport, ReportReason reason) {
        if(!userService.getUserById(reporterId).isPresent()){
            throw new UserNotFoundException();
        }else if(!reviewService.getReviewById(reviewToReport,null).isPresent()){
            throw new ReviewNotFoundException();
        }
        if(!reportDao.findAll(
                Page.with(1,1),
                new ReportFilterBuilder()
                        .withReporterId(reporterId)
                        .withReviewId(reviewToReport)
                        .withState(ReportState.UNRESOLVED)
                        .build()
        ).getList().isEmpty())
            throw new ReportAlreadyExistsException();
        Report report = reportDao.create(reporterId, reviewToReport,reason);
        LOGGER.info("User {} reported Review {} with reason {} ",reporterId,reviewToReport,reason);
        return report;
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<Report> getReports(Page page, ReportFilter filter){
        return reportDao.findAll(page,filter);
    }
    @Transactional(readOnly = true)
    @Override
    public Optional<Report> getReportById(long id) {
        return reportDao.get(id);
    }

    @Transactional
    @Override
    public Report resolveReport(long reportid, long moderatorId) {
        Report report = reportDao.get(reportid).orElseThrow(ReportNotFoundException::new);
        if (report.isResolved())
            throw new ReportAlreadyResolvedException();
        long reviewId = report.getReportedReview().getId();
        Report toReturn = updateReportStatus(report,moderatorId,true);
        LOGGER.info("Moderator {} resolved Report {} ",moderatorId,reportid);
        reviewService.deleteReviewById(reviewId,moderatorId);
        return toReturn;
    }

    @Transactional
    @Override
    public Report rejectReport(long reportid, long moderatorId) {
        Report report = reportDao.get(reportid).orElseThrow(ReportNotFoundException::new);
        if (report.isResolved())
            throw new ReportAlreadyResolvedException();
        LOGGER.info("Moderator {} rejected Report {} ",moderatorId,reportid);
        return updateReportStatus(report,moderatorId,false);
    }

    @Transactional
    @Override
    public void deleteReviewOfReports(long reviewid) {
        LOGGER.info("Deleting Reports of Review {} ",reviewid);
        reportDao.deleteReportsFromReview(reviewid);
    }

    private Report updateReportStatus(Report report, long moderatorId, boolean status){

        User moderator = userService.getUserById(moderatorId).orElseThrow(UserNotFoundException::new);
        if(!moderator.getRoles().contains(RoleType.MODERATOR)){
            throw new UserNotAModeratorException();
        }

        return reportDao.updateStatus(report.getId(),moderatorId,status);
    }

    @Transactional
    @Override
    public boolean deleteReport(long reportId){
        LOGGER.info("Deleting Report {} ",reportId);
        return reportDao.delete(reportId);
    }
}
