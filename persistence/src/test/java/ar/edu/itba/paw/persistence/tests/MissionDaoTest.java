package ar.edu.itba.paw.persistence.tests;

import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.models.MissionProgress;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistenceinterfaces.MissionDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class MissionDaoTest {
    //User fields
    private static final String USERNAME1 = "username1";

    private static final String USERNAME2 = "username2";

    private static final String PASSWORD = "password";

    private static final String EMAIL1 = "email";

    private static final String EMAIL2 = "email2";

    private static final boolean ENABLED = true;

    //Genres

    private static final Genre GENRE1 = Genre.ACTION;

    private static final Genre GENRE2 = Genre.ADVENTURE;

    private static final Genre GENRE3 = Genre.CASUAL;

    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataSource ds;

    @Autowired
    private MissionDao missionDao;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "mission_progress");
        userSetUp();
    }

    private Long userSetUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "genreforusers");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");

        SimpleJdbcInsert jdbcInsertForUsers = new SimpleJdbcInsert(ds).withTableName("users");
        SimpleJdbcInsert jdbcInsertForUsersGenres = new SimpleJdbcInsert(ds).withTableName("genreforusers");

        Map<String, Object> args = new HashMap<>();
        //Setup users
        args.put("username", USERNAME1);
        args.put("password", PASSWORD);
        args.put("email", EMAIL1);
        args.put("enabled", ENABLED);
        args.put("reputation", 0L);
        args.put("id", 1L);
        jdbcInsertForUsers.execute(args);
        args.put("username", USERNAME2);
        args.put("email", EMAIL2);
        args.put("id", 2L);
        jdbcInsertForUsers.execute(args);
        Long userId = 2L;

        //Setup user preferences
        args.clear();

        args.put("userid", userId);
        args.put("genreid", GENRE1.getId());
        jdbcInsertForUsersGenres.execute(args);
        args.put("genreid", GENRE2.getId());
        ;
        jdbcInsertForUsersGenres.execute(args);
        args.put("genreid", GENRE3.getId());
        jdbcInsertForUsersGenres.execute(args);
        return userId;
    }

    @Test
    public void createMissions() {
        User user = em.find(User.class, 1L);
        MissionProgress missionProgress = missionDao.create(user, Mission.REVIEWS_GOAL, 0f, LocalDate.now());
        Assert.assertNotNull(missionProgress);
        em.flush();
        jdbcTemplate.query("SELECT * FROM mission_progress", new Object[]{}, (rs, rowNum) -> {
            Assert.assertEquals(missionProgress.getMission().name(), rs.getString("mission"));
            Assert.assertEquals(1L, rs.getLong("userid"));
            return null;
        });
    }

    @Test
    public void findByIdExists() {
        jdbcTemplate.update("INSERT INTO mission_progress (userid, mission, progress, startdate) VALUES (?, ?, ?, ?)",
                1L, Mission.REVIEWS_GOAL.name(), 0f, Timestamp.valueOf(LocalDate.now().atStartOfDay()));
        User user = em.find(User.class, 1L);

        Optional<MissionProgress> missionProgressOptional = missionDao.findById(user, Mission.REVIEWS_GOAL);
        Assert.assertTrue(missionProgressOptional.isPresent());
        MissionProgress missionProgress = missionProgressOptional.get();
        Assert.assertEquals(missionProgress.getMission(), Mission.REVIEWS_GOAL);
        Assert.assertEquals(missionProgress.getUser(), user);
        Assert.assertEquals(missionProgress.getProgress(), (Float) 0f);
    }

    @Test
    public void findByIdNotExists() {
        User user = em.find(User.class, 1L);
        Optional<MissionProgress> missionProgressOptional = missionDao.findById(user, Mission.REVIEWS_GOAL);
        Assert.assertFalse(missionProgressOptional.isPresent());
    }

    @Test
    public void resetProgress() {
        jdbcTemplate.update("INSERT INTO mission_progress (userid, mission, progress, startdate) VALUES (?, ?, ?, ?)",
            1L, Mission.REVIEWS_GOAL.name(), 10f, Timestamp.valueOf(LocalDate.of(1, 1, 1).atStartOfDay()));
        User user = em.find(User.class, 1L);
        missionDao.resetProgress(user, Mission.REVIEWS_GOAL);
        em.flush();
        jdbcTemplate.query("SELECT * FROM mission_progress", new Object[]{}, (rs, rowNum) -> {
            Assert.assertEquals(Mission.REVIEWS_GOAL.name(), rs.getString("mission"));
            Assert.assertEquals(1L, rs.getLong("userid"));
            Assert.assertEquals(0f, rs.getFloat("progress"), 0.001);
            return null;
        });
    }

    @Test
    public void completeMissionTest() {
        jdbcTemplate.update("INSERT INTO mission_progress (userid, mission, progress, startdate, times) VALUES (?, ?, ?, ?, 0)",
            1L, Mission.REVIEWS_GOAL.name(), 10f, Timestamp.valueOf(LocalDate.of(1, 1, 1).atStartOfDay()));
        User user = em.find(User.class, 1L);
        missionDao.completeMission(user, Mission.REVIEWS_GOAL);
        em.flush();
        jdbcTemplate.query("SELECT * FROM mission_progress", new Object[]{}, (rs, rowNum) -> {
            Assert.assertEquals(Mission.REVIEWS_GOAL.name(), rs.getString("mission"));
            Assert.assertEquals(1L, rs.getLong("userid"));
            Assert.assertEquals(1, rs.getInt("times"));
            return null;
        });
    }
}
