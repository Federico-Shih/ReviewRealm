package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.enums.Mission;
import ar.edu.itba.paw.models.MissionProgress;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface MissionService {

    MissionProgress addMissionProgress(long userId, Mission mission, float progress);

    Optional<Mission> getMissionById(String missionName);

    List<Mission> getMissions(User user);

    List<MissionProgress> getMissionProgresses(long userId);

    void resetMissionsWeekly();

    void resetMissionsDaily();

    void resetMissionsMonthly();
}
