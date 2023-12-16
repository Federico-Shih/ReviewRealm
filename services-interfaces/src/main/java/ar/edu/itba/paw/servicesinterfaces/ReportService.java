package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.ReportFilter;
import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Report;

import java.util.Optional;

public interface ReportService {

    Report createReport(long reporterId, long reviewToReport, ReportReason reason);

    Optional<Report> getReportById(long id);


    boolean isReported(long reviewId, long reporterId);

    Paginated<Report> getReports(Page page, ReportFilter filter);


    Report resolveReport(long reportid,long moderatorId);

    Report rejectReport(long reportid,long moderatorId);

    void deleteReviewOfReports(long reviewid);

    boolean deleteReport(long reportId);
}
