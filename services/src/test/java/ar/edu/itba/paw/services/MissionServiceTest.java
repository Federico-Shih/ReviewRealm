package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Mission;
import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.models.MissionProgress;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.MissionDao;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Optional;

import static ar.edu.itba.paw.services.utils.UserTestModels.getUser1;
import static org.mockito.ArgumentMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class MissionServiceTest {

    private final static MissionProgress STARTING_MISSION_PROGRESS = new MissionProgress(getUser1(), Mission.RECOMMEND_GAMES, 0f, null, 0);
    private final static MissionProgress ADVANCED_MISSION_PROGRESS = new MissionProgress(getUser1(), Mission.RECOMMEND_GAMES, 1f, null, 0);
    private final static MissionProgress MORE_ADVANCED_MISSION_PROGRESS = new MissionProgress(getUser1(), Mission.RECOMMEND_GAMES, 3f, null, 0);
    private final static MissionProgress COMPLETED_MISSION_PROGRESS = new MissionProgress(getUser1(), Mission.RECOMMEND_GAMES, 5f, null, 0);
    private final static MissionProgress REPEATABLE_START_MISSION_PROGRESS = new MissionProgress(getUser1(), Mission.ACCEPTED_GAMES, 0f, null, 0);
    private final static MissionProgress REPEATED_MISSION_PROGRESS = new MissionProgress(getUser1(), Mission.ACCEPTED_GAMES, 0f, null, 5);

    @Mock
    private MissionDao missionDao;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private MissionServiceImpl missionService;

    @Test
    public void testNewMissionProgress() {
        User user = getUser1();
        user.setRoles(new HashSet<>());
        Mockito.when(missionDao.findById(any(), any())).thenReturn(Optional.empty());
        Mockito.when(userDao.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(missionDao.create(any(), any(), any(), any())).thenReturn(STARTING_MISSION_PROGRESS);
        Mockito.when(missionDao.updateProgress(any(), any(), anyFloat())).thenReturn(Optional.of(ADVANCED_MISSION_PROGRESS));

        MissionProgress missionProgress = missionService.addMissionProgress(user.getId(), Mission.RECOMMEND_GAMES, 1f);

        Assert.assertEquals(Mission.RECOMMEND_GAMES, missionProgress.getMission());
        Assert.assertEquals(1f, missionProgress.getProgress(), 0.001);
    }

    @Test
    public void testAdvanceMissionProgress() {
        User user = getUser1();
        user.setRoles(new HashSet<>());
        Mockito.when(missionDao.findById(any(), any())).thenReturn(Optional.of(ADVANCED_MISSION_PROGRESS));
        Mockito.when(userDao.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(missionDao.updateProgress(any(), any(), anyFloat())).thenReturn(Optional.of(MORE_ADVANCED_MISSION_PROGRESS));

        MissionProgress missionProgress = missionService.addMissionProgress(user.getId(), Mission.RECOMMEND_GAMES, 2f);

        Assert.assertEquals(Mission.RECOMMEND_GAMES, missionProgress.getMission());
        Assert.assertEquals(3f, missionProgress.getProgress(), 0.001);
        Assert.assertFalse(missionProgress.isCompleted());
    }

    @Test
    public void testCompleteMissionProgress() {
        User user = getUser1();
        user.setRoles(new HashSet<>());
        Mockito.when(missionDao.findById(any(), any())).thenReturn(Optional.of(ADVANCED_MISSION_PROGRESS));
        Mockito.when(userDao.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(missionDao.updateProgress(any(), any(), anyFloat())).thenReturn(Optional.of(COMPLETED_MISSION_PROGRESS));
        Mockito.when(missionDao.completeMission(any(), any())).thenReturn(Optional.of(COMPLETED_MISSION_PROGRESS));
        Mockito.when(userDao.update(anyLong(), any())).thenReturn(Optional.of(user));

        MissionProgress missionProgress = missionService.addMissionProgress(user.getId(), Mission.RECOMMEND_GAMES, 4f);

        Assert.assertEquals(Mission.RECOMMEND_GAMES, missionProgress.getMission());
        Assert.assertTrue(missionProgress.isCompleted());
    }

    @Test
    public void testIncompatibleRoleMissionProgress() {
        User user = getUser1();
        HashSet<RoleType> roles = new HashSet<>();
        roles.add(RoleType.MODERATOR);
        user.setRoles(roles);
        Mockito.when(userDao.findById(anyLong())).thenReturn(Optional.of(user));

        MissionProgress missionProgress = missionService.addMissionProgress(user.getId(), Mission.RECOMMEND_GAMES, 1f);

        Assert.assertNull(missionProgress);
    }

    @Test
    public void testRepeatableMissionProgress() {
        User user = getUser1();
        user.setRoles(new HashSet<>());
        Mockito.when(missionDao.findById(any(), any())).thenReturn(Optional.empty());
        Mockito.when(missionDao.create(any(), any(), any(), any())).thenReturn(REPEATABLE_START_MISSION_PROGRESS);
        Mockito.when(userDao.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(missionDao.updateProgress(any(), any(), anyFloat())).thenReturn(Optional.of(REPEATED_MISSION_PROGRESS));

        MissionProgress missionProgress = missionService.addMissionProgress(user.getId(), Mission.ACCEPTED_GAMES, 5f);

        Assert.assertEquals(Mission.ACCEPTED_GAMES, missionProgress.getMission());
        Assert.assertFalse(missionProgress.isCompleted());
        Assert.assertEquals(5, (int) missionProgress.getTimes());
    }

}
