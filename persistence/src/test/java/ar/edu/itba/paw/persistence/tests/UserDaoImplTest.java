package ar.edu.itba.paw.persistence.tests;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.UserFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.UserOrderCriteria;
import ar.edu.itba.paw.dtos.saving.SaveUserBuilder;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.NotificationType;
import ar.edu.itba.paw.models.FollowerFollowingCount;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.helpers.CommonRowMappers;
import ar.edu.itba.paw.persistence.tests.utils.GameTestModels;
import ar.edu.itba.paw.persistence.tests.utils.UserTestModels;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
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
import java.sql.SQLException;
import java.util.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserDaoImplTest {
    private static final String UPDATE_EMAIL = "newemail";
    private static final String UPDATE_PASSWORD = "newpassword";
    private static final boolean UPDATE_ENABLED = true;
    private static final int UPDATE_REPUTATION = 1000;
    private static final int UPDATE_XP = 20000;
    private static final int UPDATE_AVATARID = 5;
    private static final Locale UPDATE_LOCALE = Locale.CANADA;

    private static final String WRONG_EMAIL = "aaaaaaaaaaaaaaa";

    private static final String WRONG_USERNAME = "somebody";

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDao userDao;

    @PersistenceContext
    private EntityManager em;

    private User testUser;
    private User testCreateUser;
    private User testFollowingUser;
    private User testNonFollowingUser;
    private User testUserWithPref;
    private User testUserWithoutPref;
    private User disabledNotifUser;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        this.testUser = UserTestModels.getUser1();
        this.testCreateUser = UserTestModels.getCreateUser();
        this.testFollowingUser = UserTestModels.getUser1();
        this.testNonFollowingUser = UserTestModels.getUser5();
        this.testUserWithPref = UserTestModels.getUser1();
        this.testUserWithoutPref = UserTestModels.getUser5();
        this.disabledNotifUser = UserTestModels.getUser5();
    }

    @Rollback
    @Test
    public void testFindById() throws SQLException {
        //2.execute
        Optional<User> maybeUser = userDao.findById(testUser.getId());

        //3.assert
        Assert.assertTrue(maybeUser.isPresent());
        Assert.assertEquals(testUser.getId(), maybeUser.get().getId());
        Assert.assertEquals(testUser.getEmail(), maybeUser.get().getEmail());
        Assert.assertEquals(testUser.getPassword(), maybeUser.get().getPassword());
    }

    @Rollback
    @Test
    public void testFindByIdDoesNotExist() throws SQLException {

        //2.execute
        Optional<User> maybeUser = userDao.findById(-1L);

        //3.assert
        Assert.assertFalse(maybeUser.isPresent());
    }

    @Rollback
    @Test
    public void testFindAll() throws SQLException {
        Paginated<User> userlist = userDao.findAll(Page.with(1, 80), new UserFilterBuilder().build(), new Ordering<>(OrderDirection.DESCENDING, UserOrderCriteria.LEVEL));

        Assert.assertEquals(userlist.getTotalPages(), 1);
        Assert.assertEquals(userlist.getList().size(), 5);
        User[] expectedUser = {UserTestModels.getUser5(), UserTestModels.getUser1(), UserTestModels.getUser2(), UserTestModels.getUser3(), UserTestModels.getUser4()};
        Assert.assertArrayEquals(expectedUser, userlist.getList().toArray());
    }

    @Rollback
    @Test
    public void testMultipleFilter() throws SQLException {
        //2.execute
        Paginated<User> userlist = userDao.findAll(Page.with(1, 10), new UserFilterBuilder().withEmail("email2").build(), new Ordering<>(OrderDirection.DESCENDING, UserOrderCriteria.LEVEL));

        Assert.assertEquals(userlist.getTotalPages(), 1);
        Assert.assertEquals(userlist.getList().size(), 1);
        User[] expectedUsers = {UserTestModels.getUser2()};
        Assert.assertArrayEquals(expectedUsers, userlist.getList().toArray());
    }

    @Rollback
    @Test
    public void wrongPaginationTest() {
        Assert.assertThrows(RuntimeException.class, () -> {
            userDao.findAll(Page.with(0, 0), new UserFilterBuilder().build(), new Ordering<>(OrderDirection.DESCENDING, UserOrderCriteria.LEVEL));
        });

        Paginated<User> userlist = userDao.findAll(Page.with(1000000, 10), new UserFilterBuilder().build(), new Ordering<>(OrderDirection.DESCENDING, UserOrderCriteria.LEVEL));
        Assert.assertEquals(userlist.getTotalPages(), 1);
        Assert.assertEquals(userlist.getList().size(), 0);
    }


    @Rollback
    @Test
    public void testUpdate() throws SQLException {
        User user = UserTestModels.getUser2();
        SaveUserBuilder userBuilder = new SaveUserBuilder();
        Optional<User> update = userDao.update(user.getId(),
                userBuilder
                        .withEmail(UPDATE_EMAIL)
                        .withPassword(UPDATE_PASSWORD)
                        .withEnabled(UPDATE_ENABLED)
                        .withReputation((long) UPDATE_REPUTATION)
                        .withXp((float) UPDATE_XP)
                        .withAvatar((long) UPDATE_AVATARID)
                        .withLanguage(UPDATE_LOCALE)
                        .build());
        em.flush();
        Assert.assertTrue(update.isPresent());
        User dbUser = jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", new Object[]{user.getId()}, CommonRowMappers.TEST_USER_MAPPER);
        Assert.assertNotNull(dbUser);
        Assert.assertEquals(UPDATE_EMAIL, dbUser.getEmail());
        Assert.assertEquals(UPDATE_PASSWORD, dbUser.getPassword());
        Assert.assertEquals(UPDATE_REPUTATION, dbUser.getReputation(), 0.001);
        Assert.assertEquals(UPDATE_ENABLED, dbUser.isEnabled());
        Assert.assertEquals(UPDATE_XP, dbUser.getXp(), 0.001);
        Assert.assertEquals(UPDATE_AVATARID, (long)dbUser.getAvatarId());
    }

    @Rollback
    @Test
    public void testCreate(){
        User user = userDao.create(testCreateUser.getUsername(),
                testCreateUser.getEmail(),
                testCreateUser.getPassword());

        //3.assert
        Assert.assertNotNull(user);
        em.flush();
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", String.format("id = '%s'", user.getId())));
    }

    @Rollback
    @Test
    public void testFindByEmail() {
        Optional<User> user = userDao.getByEmail(testUser.getEmail());
        Assert.assertTrue(user.isPresent());
    }

    @Transactional
    @Test
    public void testFindByWrongEmail() {
        Optional<User> user = userDao.getByEmail("Another Email");
        Assert.assertFalse(user.isPresent());
    }

    @Transactional
    @Test
    public void testFindByEmailWithEmptyTable() {
        Optional<User> user = userDao.getByEmail(WRONG_EMAIL);
        Assert.assertFalse(user.isPresent());
    }

    @Transactional
    @Test
    public void testFindByUsername() {
        Optional<User> user = userDao.getByUsername(testUser.getUsername());
        Assert.assertTrue(user.isPresent());
        Assert.assertEquals(testUser.getUsername(), user.get().getUsername());
    }

    @Transactional
    @Test
    public void testFindByWrongUsername() {
        Optional<User> user = userDao.getByUsername(WRONG_USERNAME);
        Assert.assertFalse(user.isPresent());
    }

    @Rollback
    @Test
    public void testReplaceAllFavoriteGames() {
        List<Long> gameIds = Arrays.asList(GameTestModels.getSuperGameA().getId(), GameTestModels.getSuperGameB().getId());

        userDao.replaceAllFavoriteGames(testUser.getId(), gameIds);
        em.flush();

        List<Long> games = jdbcTemplate.queryForList("Select gameid from favoritegames where userid = ?", Long.class, testUser.getId());
        Assert.assertEquals(2, games.size());
        Set<Long> expectedGames = new HashSet<>(Arrays.asList(GameTestModels.getSuperGameA().getId(), GameTestModels.getSuperGameB().getId()));
        Assert.assertEquals(expectedGames, new HashSet<>(games));
    }


    @Rollback
    @Test
    public void testDeleteFavoriteGameForUser() {
        userDao.deleteFavoriteGameForUser(UserTestModels.getUser5().getId(), GameTestModels.getSuperGameA().getId());
        em.flush();

        Object[] params = {UserTestModels.getUser5().getId()};
        List<Long> gameIds = jdbcTemplate.queryForList("Select gameid from favoritegames where userid = ?", params, Long.class);
        Assert.assertEquals(2, gameIds.size());
    }

    @Rollback
    @Test
    public void testGetFollowers() {
        Optional<Paginated<User>> followers = userDao.getFollowers(testFollowingUser.getId(), Page.with(1, 100));
        Assert.assertTrue(followers.isPresent());
        Assert.assertEquals(3, followers.get().getList().size());
        List<User> expected = Arrays.asList(UserTestModels.getUser2(), UserTestModels.getUser4(), UserTestModels.getUser3());
        Assert.assertEquals(new HashSet<>(expected), new HashSet<>(followers.get().getList()));
    }


    @Rollback
    @Test
    public void testGetFollowing() {
        Optional<Paginated<User>> following = userDao.getFollowing(testFollowingUser.getId(), Page.with(1, 20));
        Assert.assertTrue(following.isPresent());
        Assert.assertEquals(1, following.get().getList().size());
        User[] expectedUsers = {UserTestModels.getUser3()};
        Assert.assertArrayEquals(expectedUsers, following.get().getList().toArray());
    }

    @Rollback
    @Test
    public void testGetFollowingCount() {
        Optional<FollowerFollowingCount> ffcount = userDao.getFollowerFollowingCount(testFollowingUser.getId());
        Assert.assertTrue(ffcount.isPresent());
        Assert.assertEquals(3, ffcount.get().getFollowerCount());
        Assert.assertEquals(1, ffcount.get().getFollowingCount());
    }

    @Rollback
    @Test
    public void testGetFollowingCountEmpty() {
        Optional<FollowerFollowingCount> ffcount = userDao.getFollowerFollowingCount(testNonFollowingUser.getId());
        Assert.assertTrue(ffcount.isPresent());
        Assert.assertEquals(0, ffcount.get().getFollowerCount());
        Assert.assertEquals(0, ffcount.get().getFollowingCount());
    }

    @Rollback
    @Test
    public void testCreateFollow() {
        Optional<User> user = userDao.createFollow(testUser.getId(), UserTestModels.getUser5().getId());
        Assert.assertTrue(user.isPresent());
        em.flush();
        int count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "followers", String.format("userid = '%s' and following = '%s'", testUser.getId(), UserTestModels.getUser5().getId()));
        Assert.assertEquals(1, count);
    }

    @Rollback
    @Test
    public void testDeleteFollow() {
        Optional<User> user = userDao.deleteFollow(testFollowingUser.getId(), UserTestModels.getUser3().getId());
        Assert.assertTrue(user.isPresent());
        Assert.assertFalse(user.get().getFollowing().contains(UserTestModels.getUser3()));
        em.flush();
        int count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "followers", String.format("userid = '%s' and following = '%s'", testFollowingUser.getId(), UserTestModels.getUser3().getId()));
        Assert.assertEquals(0, count);
    }

    @Rollback
    @Test
    public void testDeleteNonexistentFollow() {
        User supposedFollowing = UserTestModels.getUser3();
        Optional<User> user = userDao.deleteFollow(testNonFollowingUser.getId(), supposedFollowing.getId());
        Assert.assertTrue(user.isPresent());
        Assert.assertFalse(user.get().getFollowing().contains(supposedFollowing));
    }

    @Rollback
    @Test
    public void testFollows() {
        boolean user = userDao.follows(testFollowingUser.getId(), UserTestModels.getUser3().getId());
        Assert.assertTrue(user);
    }

    @Rollback
    @Test
    public void testDoesNotFollow() {
        Assert.assertFalse(userDao.follows(testNonFollowingUser.getId(), testUser.getId()));
    }

    @Rollback
    @Test
    public void testGetPreferencesWithExistingGenres() {
        Set<Genre> preferences = userDao.getPreferences(testUserWithPref.getId());
        Assert.assertEquals(2, preferences.size());
        Assert.assertTrue(preferences.contains(Genre.ACTION));
        Assert.assertTrue(preferences.contains(Genre.ADVENTURE));
    }

    @Rollback
    @Test
    public void testGetPreferencesWithNoGenres() {
        Set<Genre> preferences = userDao.getPreferences(testUserWithoutPref.getId());
        Assert.assertTrue(preferences.isEmpty());
    }

    @Rollback
    @Test
    public void testGetPreferencesNonexistentUser() {
        Set<Genre> preferences = userDao.getPreferences(-1L);
        Assert.assertTrue(preferences.isEmpty());
    }

    @Rollback
    @Test
    public void testGetTotalAmountOfUsers() {
        long amount = userDao.getTotalAmountOfUsers();
        Assert.assertEquals(5L, amount);
    }

    @Rollback
    @Test
    public void testEnableNotification() {
        userDao.enableNotification(disabledNotifUser.getId(), NotificationType.USER_I_FOLLOW_WRITES_REVIEW.getTypeName());
        em.flush();
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_disabled_notifications", String.format("userid ='%s' and notification = '%s'", disabledNotifUser.getId(), NotificationType.USER_I_FOLLOW_WRITES_REVIEW.getTypeName())));
    }

    @Rollback
    @Test
    public void testDisableNotification() {
        userDao.disableNotification(disabledNotifUser.getId(), NotificationType.MY_REVIEW_IS_DELETED.getTypeName());
        em.flush();
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "user_disabled_notifications", String.format("userid ='%s' and notification = '%s'", disabledNotifUser.getId(), NotificationType.MY_REVIEW_IS_DELETED.getTypeName())));
    }
}
