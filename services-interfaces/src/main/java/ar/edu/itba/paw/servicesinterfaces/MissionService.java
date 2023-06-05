package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.enums.Mission;
import ar.edu.itba.paw.models.User;

public interface MissionService {
    void addMissionProgress(User user, Mission mission, Float progress);
}
