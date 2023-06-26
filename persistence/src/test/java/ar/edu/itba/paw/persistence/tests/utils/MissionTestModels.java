package ar.edu.itba.paw.persistence.tests.utils;

import ar.edu.itba.paw.enums.Mission;
import ar.edu.itba.paw.models.MissionProgress;
import ar.edu.itba.paw.models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MissionTestModels {
    private MissionTestModels() {

    }
    private static final Mission MISSION = Mission.SETUP_PREFERENCES;
    private static final float PROGRESS = 1;
    private static final LocalDate START_DATE = LocalDate.of(2019, 1, 1);
    private static final int TIMES = 1;
    private static final User USER = UserTestModels.getUser3();

    private static final Mission CREATE_MISSION = Mission.REVIEWS_GOAL;
    private static final float CREATE_PROGRESS = 0;
    private static final LocalDate CREATE_START_DATE = LocalDate.of(2020, 1,  1);
    private static final User CREATE_USER = UserTestModels.getUser1();

    public static MissionProgress getMissionProgress() {
        return new MissionProgress(USER, MISSION, PROGRESS, START_DATE, TIMES);
    }

    public static MissionProgress getCreateMissionProgress() {
        return new MissionProgress(CREATE_USER, CREATE_MISSION, CREATE_PROGRESS, CREATE_START_DATE, TIMES);
    }
}
