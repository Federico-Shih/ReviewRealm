package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.saving.SaveUserBuilder;
import ar.edu.itba.paw.dtos.saving.SaveUserDTO;
import ar.edu.itba.paw.enums.Mission;
import ar.edu.itba.paw.models.MissionProgress;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.MissionDao;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import ar.edu.itba.paw.servicesinterfaces.MissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MissionServiceImpl implements MissionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MissionServiceImpl.class);
    private final MissionDao missionDao;
    private final UserDao userDao;

    @Autowired
    public MissionServiceImpl(MissionDao missionDao, UserDao userDao) {
        this.missionDao = missionDao;
        this.userDao = userDao;
    }

    @Transactional
    @Override
    public MissionProgress addMissionProgress(User user, Mission mission, Float progress) {
        if (mission.getRoleType() != null && !user.getRoles().contains(new Role(mission.getRoleType().getRole()))) {
            return null;
        }
        MissionProgress missionProgress = this.missionDao
                .findById(user, mission)
                .orElseGet(() -> this.missionDao.create(user, mission, 0f, LocalDate.now()));
        if (!missionProgress.isCompleted()) {
            missionProgress = missionDao.updateProgress(user, mission, missionProgress.getProgress() + progress);
            if (missionProgress.isCompleted()) {
                LOGGER.info("Completed mission {} for user {}, gained {} xp", mission.getTitle(), user.getId(), mission.getXp());
                missionProgress = missionDao.completeMission(user, mission);
                userDao.update(user.getId(), new SaveUserBuilder().withXp(user.getXp() + mission.getXp()).build());
                // Automatically reset repeatable and none time frequency missions
                if (mission.isRepeatable() && mission.getFrequency() == Mission.MissionFrequency.NONE) {
                    missionProgress = missionDao.resetProgress(user, mission);
                }
            }
        }
        return missionProgress;
    }

    @Scheduled(cron = "0 0 0 * * 0")
    @Transactional
    public void resetMissionsWeekly() {
        List<MissionProgress> missionProgresses = missionDao.findAll();
        for (MissionProgress missionProgress : missionProgresses) {
            if (missionProgress.getMission().getFrequency() == Mission.MissionFrequency.WEEKLY && missionProgress.getMission().isRepeatable()) {
                missionDao.resetProgress(missionProgress.getUser(), missionProgress.getMission());
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetMissionsDaily() {
        List<MissionProgress> missionProgresses = missionDao.findAll();
        for (MissionProgress missionProgress : missionProgresses) {
            if (missionProgress.getMission().getFrequency() == Mission.MissionFrequency.DAILY && missionProgress.getMission().isRepeatable()) {
                missionDao.resetProgress(missionProgress.getUser(), missionProgress.getMission());
            }
        }
    }

    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void resetMissionsMonthly() {
        List<MissionProgress> missionProgresses = missionDao.findAll();
        for (MissionProgress missionProgress : missionProgresses) {
            if (missionProgress.getMission().getFrequency() == Mission.MissionFrequency.MONTHLY && missionProgress.getMission().isRepeatable()) {
                missionDao.resetProgress(missionProgress.getUser(), missionProgress.getMission());
            }
        }
    }
}
