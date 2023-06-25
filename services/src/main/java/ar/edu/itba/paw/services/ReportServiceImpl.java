package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.ReportFilter;
import ar.edu.itba.paw.dtos.filtering.ReportFilterBuilder;
import ar.edu.itba.paw.enums.ReportReason;
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
    public ReportServiceImpl(ReportDao reportDao, UserService userService, ReviewService reviewService) {
        this.reportDao = reportDao;
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @Transactional
    @Override
    public Report createReport(long reporterId, long reviewToReport, ReportReason reason) {
        if(!userService.getUserById(reporterId).isPresent()){
            throw new UserNotFoundException("user.notfound");
        }else if(!reviewService.getReviewById(reviewToReport,null).isPresent()){
            throw new ReviewNotFoundException();
        }
        Report report = reportDao.create(reporterId, reviewToReport,reason);
        if(report==null) {
            throw new ReportAlreadyExistsException("report.notcreated");
        }
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
    public Boolean isReported(long reviewId, long reporterId){
        ReportFilter filter = new ReportFilterBuilder().withReviewId(reviewId).withReporterId(reporterId).build();
        return reportDao.findAll(Page.with(1,1),filter).getList().size()>0;
    }


    @Transactional
    @Override
    public Report resolveReport(long reportid,long moderatorId) {
       return updateReportStatus(reportid,moderatorId,true);
    }
    @Transactional
    @Override
    public Report rejectReport(long reportid, long moderatorId) {
        return updateReportStatus(reportid,moderatorId,false);
    }

    private Report updateReportStatus(long reportid, long moderatorId, boolean status){
        Report report = reportDao.get(reportid).orElseThrow(ReportNotFoundException::new);
        User moderator = userService.getUserById(moderatorId).orElseThrow(UserNotFoundException::new);
        if(!moderator.getRoles().contains(RoleType.MODERATOR)){
            throw new UserNotAModeratorException();
        }
        return reportDao.updateStatus(reportid,moderatorId,status);
    }

    @Transactional
    @Override
    public Boolean deleteReport(long reportId){
        return reportDao.delete(reportId);
    }
}
