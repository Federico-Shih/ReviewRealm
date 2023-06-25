package ar.edu.itba.paw.models;

import ar.edu.itba.paw.converters.LocalDateConverter;
import ar.edu.itba.paw.enums.Mission;
import ar.edu.itba.paw.models.keys.MissionProgressId;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "mission_progress")
@IdClass(value = MissionProgressId.class)
public class MissionProgress {
    @Transient
    private static final double ROUNDING_ERROR = 0.0001;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "userid", referencedColumnName = "id")
    private User user;

    @Id
    @Column(name = "mission")
    @Enumerated(value = EnumType.STRING)
    private Mission mission;

    @Column(name = "progress")
    private Float progress;

    @Column(name = "startdate")
    @Convert(converter = LocalDateConverter.class)
    private LocalDate startDate;

    @Column(name = "times")
    private Integer times;

    public MissionProgress(User user, Mission mission, Float progress, LocalDate startDate, Integer times) {
        this.user = user;
        this.mission = mission;
        this.progress = progress;
        this.startDate = startDate;
        this.times = times;
    }

    protected MissionProgress() {
        /* For hibernate */
    }

    public boolean isCompleted() {
        return progress >= getMission().getTarget();
    }

    public User getUser() {
        return user;
    }

    public Mission getMission() {
        return mission;
    }

    public Float getProgress() {
        return progress;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setProgress(Float progress) {
        this.progress = progress;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }
}
