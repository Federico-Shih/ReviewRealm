package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.ReportFilter;
import ar.edu.itba.paw.dtos.filtering.ReportLogFilter;
import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.keys.ReportId;
import ar.edu.itba.paw.persistence.helpers.QueryBuilder;
import ar.edu.itba.paw.persistenceinterfaces.PaginationDao;
import ar.edu.itba.paw.persistenceinterfaces.ReportDao;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ReportHibernateDao implements ReportDao, PaginationDao<ReportFilter> {


    @PersistenceContext
    private EntityManager em;

    @Override
    public Report create(long reporterId, long reviewId, ReportReason reason) {
        Review reportedReview = em.find(Review.class, reviewId);
        User reporter = em.find(User.class, reporterId);
        if(reportedReview == null || reporter == null|| reason == null) {
            return null;
        }
        Report report = new Report(reporter, reportedReview, reason, LocalDateTime.now());
        em.persist(report);
        return report;
    }


    @Override
    public Long count(ReportFilter filter) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Report> root = criteriaQuery.from(Report.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        addReportFilterQuery(filter, criteriaQuery, criteriaBuilder, root);
        final TypedQuery<Long> query = em.createQuery(criteriaQuery);
        return query.getSingleResult();
    }
    private <T> void addReportFilterQuery(ReportFilter filter, CriteriaQuery<T> criteriaQuery, CriteriaBuilder criteriaBuilder, Root<Report> root) {
        if(filter.getReporterId() != null) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("reporter"), filter.getReporterId()));
        }
        if(filter.getReviewId() != null) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("reportedReview"), filter.getReviewId()));
        }
        if(filter.getReason() != null) {
            criteriaQuery.where(criteriaBuilder.like(root.get("reason"), filter.getReason().toString()));
        }
        if(filter.getReportedUserId() != null) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("reportedUser"), filter.getReportedUserId()));
        }
        if(filter.getResolved() != null) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("resolved"), filter.getResolved()));
        }
        if(filter.getModeratorId() != null) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("moderator"), filter.getModeratorId()));
        }
    }
    private QueryBuilder getQueryBuilderFromFilter(ReportFilter filter){
        QueryBuilder queryBuilder = new QueryBuilder()
                .withExact("reporterid", filter.getReporterId())
                .withExact("reviewid", filter.getReviewId())
                .withExact("reporteduserid",filter.getReportedUserId())
                .withExact("resolved", filter.getResolved())
                .withExact("moderatorid", filter.getModeratorId())
                .withSimilar("reason", (filter.getReason() != null )? filter.getReason().toString():null);
        return queryBuilder;

    }
    @Override
    public Paginated<Report> findAll(Page page, ReportFilter filter) {
        int pages = getPageCount(filter, page.getPageSize());
        if(page.getPageNumber() > pages) {
            return new Paginated<>(page.getPageNumber(), page.getPageSize(), pages, Collections.emptyList());
        }
        QueryBuilder queryBuilder = getQueryBuilderFromFilter(filter);
        Query nativeQuery = em.createNativeQuery("SELECT id from reports " + queryBuilder.toQuery());
        prepareParametersForNativeQuery(queryBuilder, nativeQuery);
        nativeQuery.setFirstResult(page.getOffset().intValue());
        nativeQuery.setMaxResults(page.getPageSize());

        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) (List<Long>) nativeQuery.getResultList().stream().map(n -> ((Number) n).longValue()).collect(Collectors.toList());

        if(ids.isEmpty()) {
            return new Paginated<>(page.getPageNumber(), page.getPageSize(), pages, Collections.emptyList());
        }
        final TypedQuery<Report> query = em.createQuery("from Report where id in :ids ORDER BY submissionDate ASC ", Report.class);
        query.setParameter("ids", ids);
        query.setFirstResult(page.getOffset().intValue());
        query.setMaxResults(page.getPageSize());

        return new Paginated<>(page.getPageNumber(), page.getPageSize(), pages, query.getResultList());
    }


    @Override
    public boolean delete(long id) {
        Report report = em.find(Report.class,id);
        if(report == null) { return false;}
        em.remove(report);
        return true;
    }
    @Override
    public Optional<Report> get(long id) {
        return Optional.ofNullable(em.find(Report.class, id));
    }

    @Override
    public Report updateStatus(long id, long moderatorId, boolean resolved) {
        Report report = em.find(Report.class, id);
        User moderator = em.find(User.class, moderatorId);
        if(report == null || moderator == null) {
            return null;
        }
        report.setModerator(moderator);
        report.setResolvedDate(LocalDateTime.now());
        if(resolved) {
            report.setReportedReview(null);
        }
        report.setResolved(true);
        return report;
    }


    private void prepareParametersForNativeQuery(QueryBuilder queryBuilder, Query nativeQuery) {
        int length = queryBuilder.toArguments().size();
        ArrayList<Object> array = new ArrayList<>(queryBuilder.toArguments());
        for (int i = 0; i < length; i += 1) {
            nativeQuery.setParameter(i + 1, array.get(i));
        }
    }
}
