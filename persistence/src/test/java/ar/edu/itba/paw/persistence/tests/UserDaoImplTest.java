package ar.edu.itba.paw.persistence.tests;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.UserFilter;
import ar.edu.itba.paw.dtos.saving.SaveUserBuilder;
import ar.edu.itba.paw.dtos.filtering.UserFilterBuilder;
import ar.edu.itba.paw.dtos.saving.SaveUserBuilder;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.FollowerFollowingCount;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.UserDaoImpl;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserDaoImplTest {

    private final static Long ID = 1L;
    private final static String USERNAME = "username";
    private final static String EMAIL = "email";
    private final static String PASSWORD = "password";
    private final static Long ID2 = 2L;
    private final static String USERNAME2 = "username2";
    private final static String EMAIL2 = "email2";
    private final static String PASSWORD2 = "password2";
    private final static Long ID3 =3L;

    private final static boolean ENABLED = true;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDaoImpl userDao;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"users");
    }
    @Test
    public void testFindById() throws SQLException{
        //1.prepare
        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES ("+ID+",'"+USERNAME+"', '"+EMAIL+"','"+PASSWORD+"')");
        //2.execute
        Optional<User> maybeUser = userDao.findById(ID);

        //3.assert
        Assert.assertTrue(maybeUser.isPresent());
        Assert.assertEquals(ID,maybeUser.get().getId());
        Assert.assertEquals(EMAIL,maybeUser.get().getEmail());
        Assert.assertEquals(PASSWORD,maybeUser.get().getPassword());
    }
    @Test
    public void testFindByIdDoesNotExist() throws SQLException {
        //1.prepare

        //2.execute
        Optional<User> maybeUser = userDao.findById(ID);

        //3.assert
        Assert.assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testFindAll() throws SQLException {
        //1.prepare
        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES ("+ID+",'"+USERNAME+"', '"+EMAIL+"','"+PASSWORD+"')");

        //2.execute
        Paginated<User> userlist = userDao.findAll(Page.with(1, 10), new UserFilterBuilder().build());

        Assert.assertEquals(userlist.getTotalPages(), 1);
        Assert.assertTrue(userlist.getList().contains(new User(ID,USERNAME,EMAIL, PASSWORD)));
    }

    @Test
    public void testFindAllMultipleUsers() throws SQLException {
        //1.prepare
        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES (" + ID + ",'" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES (" + ID2 + ",'" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");

        //2.execute
        Paginated<User> userlist = userDao.findAll(Page.with(1, 10), new UserFilterBuilder().build());

        Assert.assertEquals(userlist.getTotalPages(), 1);
        Assert.assertTrue(userlist.getList().contains(new User(ID, USERNAME, EMAIL, PASSWORD)));
        Assert.assertTrue(userlist.getList().contains(new User(ID2,USERNAME2,EMAIL2, PASSWORD2)));
    }

    @Test
    public void testMultipleFilter() throws SQLException {
        //1.prepare
        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES ("+ID+",'"+USERNAME+"', '"+EMAIL+"','"+PASSWORD+"')");

        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES (2,'username2', 'email2','password2')");
        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES (3,'username3', 'email3','password3')");
        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES (4,'username4', 'email4','password4')");

        //2.execute
        Paginated<User> userlist = userDao.findAll(Page.with(1, 10), new UserFilterBuilder().withEmail("email2").build());

        Assert.assertEquals(userlist.getTotalPages(), 1);
        Assert.assertTrue(userlist.getList().contains(new User(2L,"username2", "email4", "password4")));
    }
    @Test
    public void wrongPaginationTest() {
        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES ("+ID+",'"+USERNAME+"', '"+EMAIL+"','"+PASSWORD+"')");
        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES (2,'username2', 'email2','password2')");
        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES (3,'username3', 'email3','password3')");
        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES (4,'username4', 'email4','password4')");
        Assert.assertThrows(RuntimeException.class, () -> {
            userDao.findAll(Page.with(0, 0), new UserFilterBuilder().build());
        });

        Paginated<User> userlist = userDao.findAll(Page.with(1000000, 10), new UserFilterBuilder().build());
        Assert.assertEquals(userlist.getTotalPages(), 1);
        Assert.assertEquals(userlist.getList().size(), 0);
    }


    @Test
    public void testUpdate() throws SQLException {
        jdbcTemplate.execute("INSERT INTO users (id,username,email,password, enabled) VALUES ("+ID+",'"+USERNAME+"', '"+EMAIL+"','"+PASSWORD+"', "+ ENABLED +")");

        SaveUserBuilder userBuilder = new SaveUserBuilder();
        int update = userDao.update(ID, userBuilder.withEmail("newEmail").withPassword("newPassword").withEnabled(false).withReputation(1000L).build());

        Assert.assertEquals(update, 1);
        jdbcTemplate.query("SELECT * FROM users WHERE id = ?", new Object[]{ID}, rs -> {
            Assert.assertEquals(rs.getString("email"), "newEmail");
            Assert.assertEquals(rs.getString("password"), "newPassword");
            Assert.assertFalse(rs.getBoolean("enabled"));
            Assert.assertEquals(rs.getLong("reputation"), 1000L);
        });
    }

    @Test
    public void testCreate(){
        //1.prepare

        //2.execute
        User user = userDao.create(USERNAME,EMAIL,PASSWORD);

        //3.assert
        Assert.assertNotNull(user);
        Assert.assertEquals(EMAIL,user.getEmail());
        Assert.assertEquals(PASSWORD,user.getPassword());

        Assert.assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate,"users"));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testWrongCreate() {
        //2.execute
        User user = userDao.create(USERNAME,EMAIL,null);
    }

    @Test(expected = DuplicateKeyException.class)
    public void testWrongCreateRepeatUsername() {
        //2.execute
        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES ("+ID+",'"+USERNAME+"', '"+EMAIL+"','"+PASSWORD+"')");
        User user = userDao.create(USERNAME,EMAIL,PASSWORD);
    }

    @Test
    public void testExist() {
        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES ("+ID+",'"+USERNAME+"', '"+EMAIL+"','"+PASSWORD+"')");
        boolean value = userDao.exists(ID);
        Assert.assertTrue(value);
    }

    @Test
    public void testDoesNotExist() {
        boolean value = userDao.exists(ID);
        Assert.assertFalse(value);
    }

    @Test
    public void testFindByEmail() {
        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES ("+ID+",'"+USERNAME+"', '"+EMAIL+"','"+PASSWORD+"')");
        Optional<User> user = userDao.getByEmail(EMAIL);
        Assert.assertTrue(user.isPresent());
    }

    @Test
    public void testFindByWrongEmail() {
        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES ("+ID+",'"+USERNAME+"', '"+EMAIL+"','"+PASSWORD+"')");
        Optional<User> user = userDao.getByEmail("Another Email");
        Assert.assertFalse(user.isPresent());
    }

    @Test
    public void testFindByEmailWithEmptyTable() {
        Optional<User> user = userDao.getByEmail(EMAIL);
        Assert.assertFalse(user.isPresent());
    }

    @Test
    public void testFindByUsername() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        Optional<User> user = userDao.getByUsername(USERNAME);
        Assert.assertTrue(user.isPresent());
        Assert.assertEquals(USERNAME, user.get().getUsername());
    }

    @Test
    public void testFindByWrongUsername() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        Optional<User> user = userDao.getByUsername("AnotherUsername");
        Assert.assertFalse(user.isPresent());
    }

    @Test
    public void testFindByUsernameWithEmptyTable() {
        Optional<User> user = userDao.getByUsername(USERNAME);
        Assert.assertFalse(user.isPresent());
    }

    @Test
    public void testGetFollowers() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID2 + ", '" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");
        // ID2 follows ID
        jdbcTemplate.execute("INSERT INTO followers(userid, following) VALUES ("+ ID2 +", "+ ID +")");

        List<User> followers = userDao.getFollowers(ID);
        Assert.assertEquals(ID2, followers.get(0).getId());
    }

    @Test
    public void testGetMultipleFollowers() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID2 + ", '" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID3 + ", '" + "USERNAME3" + "', '" + "EMAIL3" + "','" + "PASSWORD3" + "')");

        // ID2 and ID3 follow ID
        jdbcTemplate.execute("INSERT INTO followers(userid, following) VALUES ("+ ID2 +", "+ ID +")");
        jdbcTemplate.execute("INSERT INTO followers(userid, following) VALUES ("+ ID3 +", "+ ID +")");

        List<User> followers = userDao.getFollowers(ID);
        Assert.assertEquals(2, followers.size());
        Assert.assertTrue(followers.stream().allMatch(user -> user.getId().equals(ID2) || user.getId().equals(ID3)));
    }

    @Test
    public void testGetFollowing() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID2 + ", '" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");
        // ID follows ID2
        jdbcTemplate.execute("INSERT INTO followers(userid, following) VALUES (" + ID + ", " + ID2 + ")");

        List<User> following = userDao.getFollowing(ID);
        Assert.assertEquals(1, following.size());
        Assert.assertEquals(ID2, following.get(0).getId());
    }

    @Test
    public void testGetMultipleFollowing() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID2 + ", '" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID3 + ", '" + "USERNAME3" + "', '" + "EMAIL3" + "','" + "PASSWORD3" + "')");

        // ID follows ID2 and ID3
        jdbcTemplate.execute("INSERT INTO followers(userid, following) VALUES (" + ID + ", " + ID2 + ")");
        jdbcTemplate.execute("INSERT INTO followers(userid, following) VALUES (" + ID + ", " + ID3 + ")");

        List<User> following = userDao.getFollowing(ID);
        Assert.assertEquals(2, following.size());
        Assert.assertTrue(following.stream().allMatch(user -> user.getId().equals(ID2) || user.getId().equals(ID3)));
    }

    @Test
    public void testGetFollowingCount() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID2 + ", '" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID3 + ", '" + "USERNAME3" + "', '" + "EMAIL3" + "','" + "PASSWORD3" + "')");

        // ID2 and ID3 follow ID
        jdbcTemplate.execute("INSERT INTO followers(userid, following) VALUES ("+ ID2 +", "+ ID +")");
        jdbcTemplate.execute("INSERT INTO followers(userid, following) VALUES ("+ ID3 +", "+ ID +")");

        FollowerFollowingCount ffcount = userDao.getFollowerFollowingCount(ID);

        Assert.assertEquals(2, ffcount.getFollowerCount());
        Assert.assertEquals(0, ffcount.getFollowingCount());
    }

    @Test
    public void testGetFollowingCountEmpty() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID2 + ", '" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID3 + ", '" + "USERNAME3" + "', '" + "EMAIL3" + "','" + "PASSWORD3" + "')");

        FollowerFollowingCount ffcount = userDao.getFollowerFollowingCount(ID);

        Assert.assertEquals(0, ffcount.getFollowerCount());
        Assert.assertEquals(0, ffcount.getFollowingCount());
    }

    @Test
    public void testDeleteFollow() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID2 + ", '" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");

        // ID2 follow ID
        jdbcTemplate.execute("INSERT INTO followers(userid, following) VALUES ("+ ID2 +", "+ ID +")");

        Assert.assertTrue(userDao.deleteFollow(ID2,ID));

    }

    @Test
    public void testDeleteNonexistentFollow() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID2 + ", '" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");

        // ID2 follow ID
        jdbcTemplate.execute("INSERT INTO followers(userid, following) VALUES ("+ ID2 +", "+ ID +")");

        Assert.assertFalse(userDao.deleteFollow(ID,ID2));

    }

    @Test
    public void testFollows() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID2 + ", '" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");

        // ID2 follows ID
        jdbcTemplate.execute("INSERT INTO followers(userid, following) VALUES (" + ID2 + ", " + ID + ")");

        Assert.assertTrue(userDao.follows(ID2, ID));
    }

    @Test
    public void testDoesNotFollow() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID2 + ", '" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");

        // ID2 follows ID
        jdbcTemplate.execute("INSERT INTO followers(userid, following) VALUES (" + ID2 + ", " + ID + ")");

        Assert.assertFalse(userDao.follows(ID, ID2));
    }

    @Test
    public void testFollowsNonexistent() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID2 + ", '" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");

        Assert.assertFalse(userDao.follows(ID, ID2));
        Assert.assertFalse(userDao.follows(ID2, ID));
    }

    @Test
    public void testGetPreferencesWithExistingGenres() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");

        jdbcTemplate.execute("INSERT INTO genreforusers (userId, genreId) VALUES (" + ID + ", 1)");
        jdbcTemplate.execute("INSERT INTO genreforusers (userId, genreId) VALUES (" + ID + ", 2)");
        jdbcTemplate.execute("INSERT INTO genreforusers (userId, genreId) VALUES (" + ID + ", 3)");

        Set<Genre> preferences = userDao.getPreferences(ID);
        Assert.assertEquals(3, preferences.size());
        Assert.assertTrue(preferences.contains(Genre.ACTION));
        Assert.assertTrue(preferences.contains(Genre.ADVENTURE));
        Assert.assertTrue(preferences.contains(Genre.CASUAL));
    }

    @Test
    public void testGetPreferencesWithNoGenres() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");

        Set<Genre> preferences = userDao.getPreferences(ID);
        Assert.assertTrue(preferences.isEmpty());
    }

    @Test
    public void testGetPreferencesNonexistentUser() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");

        Set<Genre> preferences = userDao.getPreferences(ID2);
        Assert.assertTrue(preferences.isEmpty());
    }

    @Test
    public void testGetTotalAmountOfUsers() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID2 + ", '" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID3 + ", '" + "USERNAME3" + "', '" + "EMAIL3" + "','" + "PASSWORD3" + "')");
        long amount = userDao.getTotalAmountOfUsers();
        Assert.assertEquals(3L, amount);
    }

    @Test
    public void testGetTotalAmountOfUsersEmptyTable() {
        long amount = userDao.getTotalAmountOfUsers();
        Assert.assertEquals(0L, amount);
    }

    @Test
    public void testCount() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID2 + ", '" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID3 + ", '" + "USERNAME3" + "', '" + "EMAIL3" + "','" + "PASSWORD3" + "')");

        long count = userDao.count(new UserFilter(null,null,null,null,null,null));

        Assert.assertEquals(3L, count);
    }

    @Test
    public void testCountWithEmailFilter() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID2 + ", '" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID3 + ", '" + "USERNAME3" + "', '" + "email@example.com" + "','" + "PASSWORD3" + "')");

        UserFilter filter = new UserFilter(null, null, "email@example.com", null, null, null);
        long count = userDao.count(filter);

        Assert.assertEquals(1L, count);
    }

    @Test
    public void testCountWithUsernameFilter() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID2 + ", '" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID3 + ", '" + "USERNAME3" + "', '" + "EMAIL3" + "','" + "PASSWORD3" + "')");

        UserFilter filter = new UserFilter(null, USERNAME2, null, null, null, null);
        long count = userDao.count(filter);

        Assert.assertEquals(1L, count);
    }

    @Test
    public void testCountWithIdFilter() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID2 + ", '" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID3 + ", '" + "USERNAME3" + "', '" + "EMAIL3" + "','" + "PASSWORD3" + "')");

        UserFilter filter = new UserFilter(ID2, null, null, null, null, null);
        long count = userDao.count(filter);

        Assert.assertEquals(1L, count);
    }

    @Test
    public void testCountWithMultipleFilters() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID2 + ", '" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID3 + ", '" + "USERNAME3" + "', '" + "EMAIL3" + "','" + "PASSWORD3" + "')");

        UserFilter filter = new UserFilter(ID, USERNAME, EMAIL, null, null, null);
        long count = userDao.count(filter);

        Assert.assertEquals(1L, count);
    }

    @Test
    public void testCountWithMultipleResults() {
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID + ", '" + USERNAME + "', '" + EMAIL + "','" + PASSWORD + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID2 + ", '" + USERNAME2 + "', '" + EMAIL2 + "','" + PASSWORD2 + "')");
        jdbcTemplate.execute("INSERT INTO users (id, username, email, password) VALUES (" + ID3 + ", '" + "USERNAME3" + "', '" + "EMAIL3" + "','" + "PASSWORD3" + "')");

        UserFilter filter = new UserFilter(null, null, null, null, null, "user");
        long count = userDao.count(filter);

        Assert.assertEquals(3L, count);
    }
}
