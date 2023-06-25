package ar.edu.itba.paw.dtos.filtering;

import ar.edu.itba.paw.enums.ReportReason;

import java.time.LocalDateTime;

public class ReportLogFilterBuilder {

    private Long userId = null;

    private Long moderatorId = null;

    private ReportReason reason = null;

    private LocalDateTime from = null;

    private LocalDateTime to = null;

    public ReportLogFilterBuilder withUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public ReportLogFilterBuilder withModeratorId(Long moderatorId) {
        this.moderatorId = moderatorId;
        return this;
    }

    public ReportLogFilterBuilder withReason(ReportReason reason) {
        this.reason = reason;
        return this;
    }
    public ReportLogFilterBuilder withFrom(LocalDateTime from) {
        this.from = from;
        return this;
    }
    public ReportLogFilterBuilder withTo(LocalDateTime to) {
        this.to = to;
        return this;
    }


    public ReportLogFilter build() {
        return new ReportLogFilter(userId, reason, moderatorId, from, to);
    }
}
