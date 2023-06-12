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

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class MissionServiceTest {

    @Mock
    private MissionDao missionDao;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private MissionServiceImpl missionService;

    @Test
    public void testNewMissionProgress() {
        Mockito.when(missionDao.findById(any(), any())).thenReturn(Optional.empty());
        User user = new User("", "", "");
        Mockito.when(missionDao.create(any(), any(), any(), any())).thenReturn(new MissionProgress(user, Mission.ACCEPTED_GAMES, 1f, null, 0));
        user.setRoles(new HashSet<>());
        MissionProgress missionProgress = missionService.addMissionProgress(user.getId(), Mission.ACCEPTED_GAMES, 1f);
        Assert.assertEquals(Mission.ACCEPTED_GAMES, missionProgress.getMission());
        Assert.assertEquals(1f, missionProgress.getProgress(), 0.001);
    }

    @Test
    public void testIncompatibleRoleMissionProgress() {
        User user = new User("", "", "");
        HashSet<RoleType> roles = new HashSet<>();
        roles.add(RoleType.MODERATOR);
        user.setRoles(roles);
        MissionProgress missionProgress = missionService.addMissionProgress(user.getId(), Mission.RECOMMEND_GAMES, 1f);
        Assert.assertNull(missionProgress);
    }

//    @Test
//    public void missionCompletedNewProgress() {
//        User user = new User(1L,"", "", "");
//        user.setRoles(new HashSet<>());
//        Mockito.when(missionDao.findById(any(), any())).thenReturn(Optional.of(new MissionProgress(user, Mission.SETUP_PREFERENCES, 0f, null, 0)));
//        Mockito.when(missionDao.updateProgress(any(), any(), any())).thenReturn(new MissionProgress(user, Mission.SETUP_PREFERENCES, 1f, null, 0));
//        Mockito.when(missionDao.completeMission(any(), any())).thenReturn(new MissionProgress(user, Mission.SETUP_PREFERENCES, 1f, null, 1));
//        MissionProgress missionProgress = missionService.addMissionProgress(user.getId(), Mission.SETUP_PREFERENCES, 1f);
//        Assert.assertEquals(Mission.SETUP_PREFERENCES, missionProgress.getMission());
//        Assert.assertEquals(1f, missionProgress.getProgress(), 0.001);
//        Assert.assertEquals(1, (int)missionProgress.getTimes());
//    }

//    @Test
//    public void missionCompletedResetedProgress() {
//        User user = new User(1L,"", "", "");
//        user.setRoles(new HashSet<>());
//        Mockito.when(missionDao.findById(any(), any())).thenReturn(Optional.of(new MissionProgress(user, Mission.REVIEWS_GOAL, 0f, null, 0)));
//        Mockito.when(missionDao.updateProgress(any(), any(), any())).thenReturn(new MissionProgress(user, Mission.REVIEWS_GOAL, 5f, null, 0));
//        Mockito.when(missionDao.completeMission(any(), any())).thenReturn(new MissionProgress(user, Mission.REVIEWS_GOAL, 5f, null, 1));
//        Mockito.when(missionDao.resetProgress(any(), any())).thenReturn(new MissionProgress(user, Mission.REVIEWS_GOAL, 0f, null, 1));
//        MissionProgress missionProgress = missionService.addMissionProgress(user.getId(), Mission.REVIEWS_GOAL, 1f);
//        Assert.assertEquals(Mission.REVIEWS_GOAL, missionProgress.getMission());
//        Assert.assertEquals(0f, missionProgress.getProgress(), 0.001);
//        Assert.assertEquals(1, (int)missionProgress.getTimes());
//    }
}
