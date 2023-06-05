package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.enums.Mission;
import ar.edu.itba.paw.models.MissionProgress;
import ar.edu.itba.paw.models.User;

import java.time.LocalDate;
import java.util.Optional;

public interface MissionDao {
    MissionProgress create(User user, Mission mission, Float progress, LocalDate date);

    Optional<MissionProgress> findById(User user, Mission mission);

    MissionProgress updateProgress(User user, Mission mission, Float progress);

    MissionProgress resetProgress(User user, Mission mission);

    MissionProgress completeMission(User user, Mission mission);
}
