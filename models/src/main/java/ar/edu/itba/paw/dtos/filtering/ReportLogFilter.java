package ar.edu.itba.paw.dtos.filtering;

import ar.edu.itba.paw.enums.ReportReason;

import java.time.LocalDateTime;

public class ReportLogFilter {

    private final Long userId;

    private final ReportReason reason;

    private final Long moderatorId;

    private final LocalDateTime from;

    private final LocalDateTime to;

    public ReportLogFilter(Long userId, ReportReason reason, Long moderatorId, LocalDateTime from, LocalDateTime to) {
        this.userId = userId;
        this.reason = reason;
        this.moderatorId = moderatorId;
        this.from = from;
        this.to = to;
    }

    public Long getUserId() {
        return userId;
    }

    public ReportReason getReason() {
        return reason;
    }

    public Long getModeratorId() {
        return moderatorId;
    }

    public LocalDateTime getFrom() { return from; }

    public LocalDateTime getTo() { return to; }
}
