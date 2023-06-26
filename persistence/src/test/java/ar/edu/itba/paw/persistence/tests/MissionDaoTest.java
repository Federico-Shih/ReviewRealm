package ar.edu.itba.paw.persistence.tests;

import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.models.MissionProgress;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.tests.utils.MissionTestModels;
import ar.edu.itba.paw.persistenceinterfaces.MissionDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class MissionDaoTest {
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataSource ds;

    @Autowired
    private MissionDao missionDao;
    private MissionProgress createMission;
    private MissionProgress testMission;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        createMission = MissionTestModels.getCreateMissionProgress();
        testMission = MissionTestModels.getMissionProgress();
    }

    @Rollback
    @Test
    public void createMissions() {
        User user = em.find(User.class, createMission.getUser().getId());
        MissionProgress missionProgress = missionDao.create(
                user,
                createMission.getMission(),
                createMission.getProgress(),
                createMission.getStartDate());

        Assert.assertNotNull(missionProgress);
        em.flush();
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "mission_progress",
                String.format("userid = %d AND mission = '%s'", user.getId(), createMission.getMission().name())));
    }

    @Rollback
    @Test
    public void findByIdExists() {

        User user = em.find(User.class, testMission.getUser().getId());

        Optional<MissionProgress> missionProgressOptional = missionDao.findById(user, testMission.getMission());
        Assert.assertTrue(missionProgressOptional.isPresent());
        MissionProgress missionProgress = missionProgressOptional.get();
        Assert.assertEquals(testMission.getMission(), missionProgress.getMission());
        Assert.assertEquals(testMission.getUser(), missionProgress.getUser());
        Assert.assertEquals(testMission.getProgress(), missionProgress.getProgress(), 0.001);
    }

    @Rollback
    @Test
    public void findByIdNotExists() {
        User user = em.find(User.class, -1L);
        Optional<MissionProgress> missionProgressOptional = missionDao.findById(user, Mission.REVIEWS_GOAL);
        Assert.assertFalse(missionProgressOptional.isPresent());
    }

    @Rollback
    @Test
    public void resetProgress() {
        User user = em.find(User.class, testMission.getUser().getId());
        missionDao.resetProgress(user, testMission.getMission());
        em.flush();
        jdbcTemplate.query("SELECT * FROM mission_progress", new Object[]{}, (rs, rowNum) -> {
            Assert.assertEquals(testMission.getMission().name(), rs.getString("mission"));
            Assert.assertEquals((long)user.getId(), rs.getLong("userid"));
            Assert.assertEquals(0f, rs.getFloat("progress"), 0.001);
            return null;
        });
    }

    @Test
    public void completeMissionTest() {
        User user = em.find(User.class, testMission.getUser().getId());
        missionDao.completeMission(user, testMission.getMission());
        em.flush();
        jdbcTemplate.query(
                "SELECT * FROM mission_progress WHERE userid = ? and mission = ?",
                new Object[]{ user.getId(), testMission.getMission().name()},
                (rs, rowNum) -> {
                    Assert.assertEquals(testMission.getMission().name(), rs.getString("mission"));
                    Assert.assertEquals((long)testMission.getUser().getId(), rs.getLong("userid"));
                    Assert.assertEquals(testMission.getTimes() + 1, rs.getInt("times"));
                    return null;
                }
            );
    }
}
