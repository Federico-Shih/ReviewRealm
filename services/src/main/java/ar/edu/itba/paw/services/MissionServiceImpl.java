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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class MissionServiceImpl implements MissionService {

    private final MissionDao missionDao;
    private final UserDao userDao;

    @Autowired
    public MissionServiceImpl(MissionDao missionDao, UserDao userDao) {
        this.missionDao = missionDao;
        this.userDao = userDao;
    }

    @Transactional
    @Override
    public void addMissionProgress(User user, Mission mission, Float progress) {
        if (!user.getRoles().contains(new Role(mission.getRoleType().getRole()))) {
            return;
        }
        MissionProgress missionProgress = this.missionDao
                .findById(user, mission)
                .orElseGet(() -> this.missionDao.create(user, mission, 0f, LocalDate.now()));
        if (!missionProgress.isCompleted()) {
            missionProgress = missionDao.updateProgress(user, mission, missionProgress.getProgress() + progress);
            if (missionProgress.isCompleted()) {
                userDao.update(user.getId(), new SaveUserBuilder().withXp(user.getXp() + mission.getXp()).build());
                // Automatically reset repeatable and none time frequency missions
                if (mission.isRepeatable() && mission.getFrequency() == Mission.MissionFrequency.NONE) {
                    missionDao.resetProgress(user, mission);
                }
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * 0")
    @Transactional
    public void resetMissionsWeekly() {

    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetMissionsDaily() {

    }

    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void resetMissionsMonthly() {

    }
}
