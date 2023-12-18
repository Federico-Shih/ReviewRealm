package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.PaginationTotals;
import ar.edu.itba.paw.dtos.filtering.ReportFilter;
import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.enums.ReportState;
import ar.edu.itba.paw.models.*;
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
        if(reportedReview == null || reportedReview.getDeleted() || reporter == null || reason == null) {
            return null;
        }
        Report report = new Report(reporter, reportedReview, reason, LocalDateTime.now());
        em.persist(report);
        return report;
    }


    @Override
    public long count(ReportFilter filter) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Report> root = criteriaQuery.from(Report.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        addReportFilterQuery(filter, criteriaQuery, criteriaBuilder, root);
        final TypedQuery<Long> query = em.createQuery(criteriaQuery);
        Long result = query.getSingleResult();
        return result == null? 0: result;
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
        if(filter.getState() != null) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("state"), filter.getState()));
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
                .withExact("state", (filter.getState() != null)? filter.getState().toString():null)
                .withExact("moderatorid", filter.getModeratorId())
                .withSimilar("reason", (filter.getReason() != null )? filter.getReason().toString():null);

        return queryBuilder;

    }
    @Override
    public Paginated<Report> findAll(Page page, ReportFilter filter) {
        PaginationTotals totals = getPaginationTotals(filter, page.getPageSize());
        if(page.getPageNumber() > totals.getTotalPages()) {
            return new Paginated<>(page.getPageNumber(), page.getPageSize(), totals.getTotalPages(), totals.getTotalElements(), Collections.emptyList());
        }
        QueryBuilder queryBuilder = getQueryBuilderFromFilter(filter);
        Query nativeQuery = em.createNativeQuery("SELECT id from reports " + queryBuilder.toQuery());
        prepareParametersForNativeQuery(queryBuilder, nativeQuery);
        nativeQuery.setFirstResult(page.getOffset());
        nativeQuery.setMaxResults(page.getPageSize());

        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) nativeQuery.getResultList().stream().map(n -> ((Number) n).longValue()).collect(Collectors.toList());

        if(ids.isEmpty()) {
            return new Paginated<>(page.getPageNumber(), page.getPageSize(), totals.getTotalPages(), totals.getTotalElements(), Collections.emptyList());
        }
        final TypedQuery<Report> query = em.createQuery("from Report where id in :ids ORDER BY submissionDate ASC ", Report.class);
        query.setParameter("ids", ids);

        return new Paginated<>(page.getPageNumber(), page.getPageSize(), totals.getTotalPages(), totals.getTotalElements(), query.getResultList());
    }


    @Override
    public boolean delete(long id) {
        Report report = em.find(Report.class,id);
        if(report == null) { return false;}
        em.remove(report);
        return true;
    }

    @Override
    public long deleteReportsFromReview(long reviewId) {
        Review review = em.find(Review.class, reviewId);
        if(review == null) return 0;
        final Query query = em.createQuery("update Report set state = 'DELETED_REVIEW' where reportedReview = :review");
        query.setParameter("review", review);
        return query.executeUpdate();
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
        report.setState(resolved? ReportState.ACCEPTED: ReportState.REJECTED);
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
