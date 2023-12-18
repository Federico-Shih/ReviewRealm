package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.ReportFilter;
import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Report;

import java.util.Optional;

public interface ReportDao {

    Report create(long reporterId, long reviewId, ReportReason reason);

    Paginated<Report> findAll(Page page, ReportFilter filter);

    boolean delete(long id);

    Optional<Report> get(long id);

    Report updateStatus(long id, long moderatorId, boolean resolved);

    long deleteReportsFromReview(long reviewId);


}
