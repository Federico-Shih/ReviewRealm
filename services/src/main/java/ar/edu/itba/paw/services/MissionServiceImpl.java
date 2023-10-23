package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.saving.SaveUserBuilder;
import ar.edu.itba.paw.enums.Mission;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.MissionProgress;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.MissionDao;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import ar.edu.itba.paw.servicesinterfaces.MailingService;
import ar.edu.itba.paw.servicesinterfaces.MissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MissionServiceImpl implements MissionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MissionServiceImpl.class);
    private final MissionDao missionDao;
    private final UserDao userDao;
    private final MailingService mailingService;

    @Autowired
    public MissionServiceImpl(MissionDao missionDao, UserDao userDao, MailingService mailingService) {
        this.missionDao = missionDao;
        this.userDao = userDao;
        this.mailingService = mailingService;
    }

    @Transactional
    @Override
    public MissionProgress addMissionProgress(long userId, Mission mission, float progress) {
        User user = this.userDao.findById(userId).orElseThrow(IllegalArgumentException::new);
        if (mission.getRoleType() != null && !user.getRoles().contains(mission.getRoleType())) {
            return null;
        }
        User lambdaUser = user;
        MissionProgress missionProgress = this.missionDao
                .findById(user, mission)
                .orElseGet(() -> this.missionDao.create(lambdaUser, mission, 0f, LocalDate.now()));
        // Completar misiones pendientes en caso de cambios de misiones
        if (missionProgress.isCompleted() && mission.isRepeatable() && mission.getFrequency().equals(Mission.MissionFrequency.NONE)) {
            Float currentProgress = missionProgress.getProgress();
            double completed = Math.floor(currentProgress / mission.getTarget());
            for (int i = 0; i < completed; i += 1) {
                missionProgress = missionDao.completeMission(user, mission).orElseThrow(IllegalArgumentException::new);
            }
            user = userDao.update(
                    user.getId(),
                    new SaveUserBuilder()
                            .withXp(
                                    user.getXp() + (int) (completed * mission.getXp()))
                            .build())
                    .orElseThrow(() -> {
                LOGGER.error("Could not update user {} with {} xp", lambdaUser.getId(), mission.getXp());
                return new UserNotFoundException();
            });
            missionProgress = missionDao.updateProgress(user, mission, currentProgress % mission.getTarget()).orElseThrow(IllegalArgumentException::new);
        }
        if (!missionProgress.isCompleted()) {
            missionProgress = missionDao.updateProgress(user, mission, missionProgress.getProgress() + progress).orElseThrow(IllegalArgumentException::new);
            if (missionProgress.isCompleted()) {
                LOGGER.info("Completed mission {} for user {}, gained {} xp", mission.getTitle(), user.getId(), mission.getXp());
                missionProgress = missionDao.completeMission(user, mission).orElseThrow(IllegalArgumentException::new);
                int level = user.getLevel();
                User updatedUser = userDao.update(user.getId(), new SaveUserBuilder().withXp(user.getXp() + mission.getXp()).build()).orElseThrow(() -> {
                    LOGGER.error("Could not update user {} with {} xp", lambdaUser.getId(), mission.getXp());
                    return new UserNotFoundException();
                });
                if (updatedUser.getLevel() != level) {
                    LOGGER.info("User {} leveled up to level {}", user.getId(), updatedUser.getLevel());
                    this.mailingService.sendLevelUpEmail(updatedUser);
                }
                // Automatically reset repeatable and none time frequency missions
                if (mission.isRepeatable() && mission.getFrequency() == Mission.MissionFrequency.NONE) {
                    missionProgress = missionDao.resetProgress(user, mission).orElseThrow(IllegalArgumentException::new);
                }
            }
        }
        return missionProgress;
    }

    @Transactional
    @Override
    public Optional<Mission> getMissionById(String missionName) {
        for (Mission mission : Mission.values()) {
            if (mission.name().equals(missionName)) {
                return Optional.of(mission);
            }
        }
        return Optional.empty();
    }

    @Transactional
    @Override
    public List<Mission> getMissions(User user) {
        return Arrays.stream(Mission.values()).filter((mission -> mission.getRoleType() == null || (user != null && user.getRoles().stream().anyMatch((roleType -> roleType.equals(mission.getRoleType())))))).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<MissionProgress> getMissionProgresses(long userId) {
        return this.userDao.findById(userId).orElseThrow(UserNotFoundException::new).getMissions();
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
