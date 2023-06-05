package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.enums.Mission;
import ar.edu.itba.paw.models.MissionProgress;
import ar.edu.itba.paw.models.User;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public interface MissionService {
    void addMissionProgress(User user, Mission mission, Float progress);
    void resetMissionsWeekly();
    void resetMissionsDaily();
    void resetMissionsMonthly();
}
