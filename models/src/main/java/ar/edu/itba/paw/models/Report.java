package ar.edu.itba.paw.models;

import ar.edu.itba.paw.converters.LocalDateTimeConverter;
import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.models.keys.ReportId;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_id_seq")
    @SequenceGenerator(sequenceName = "report_id_seq", name = "report_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reporterid", referencedColumnName = "id")
    private User reporter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewid", referencedColumnName = "id")
    private Review reportedReview;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reporteduserid", referencedColumnName = "id")
    private User reportedUser;

    @Column(name = "reason")
    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @Column(name = "submissionDate", nullable = false)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime submissionDate;

    @Column(name = "resolved")
    private boolean resolved;

    @Column(name = "resolvedDate")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime resolvedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderatorId", referencedColumnName = "id")
    private User moderator;

    protected Report() {
        /* For hibernate */
    }

    public Report(User reporter, Review reportedReview, ReportReason reason, LocalDateTime submissionDate) {
        this.reporter = reporter;
        this.reportedReview = reportedReview;
        this.reason = reason;
        this.reportedUser = reportedReview.getAuthor();
        this.submissionDate = submissionDate;
    }

    public User getReporter() {
        return reporter;
    }

    public Review getReportedReview() {
        return reportedReview;
    }

    public ReportReason getReason() {
        return reason;
    }

    public LocalDateTime getSubmissionDate() { return submissionDate; }

    public Long getId() {
        return id;
    }

    public boolean isResolved() {
        return resolved;
    }

    public LocalDateTime getResolvedDate() {
        return resolvedDate;
    }

    public User getModerator() {
        return moderator;
    }

    public void setReportedUser(User reportedUser) {
        this.reportedUser = reportedUser;
    }

    public void setReportedReview(Review reportedReview) {
        this.reportedReview = reportedReview;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public void setResolvedDate(LocalDateTime resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }

    public User getReportedUser() {
        return reportedUser;
    }
}
