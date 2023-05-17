package ar.edu.itba.paw.persistence.tests;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.UserFilterBuilder;
import ar.edu.itba.paw.dtos.saving.SaveUserBuilder;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.UserDaoImpl;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Optional;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserDaoImplTest {

    private final static Long ID = 1L;

    private final static String USERNAME = "username";
    private final static String EMAIL = "email";
    private final static String PASSWORD = "password";

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


//    @Test
//    public void testUserSearch() {
//        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES ("+ID+",'"+USERNAME+"', '"+EMAIL+"','"+PASSWORD+"')");
//        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES (2,'username2', 'email2','password2')");
//        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES (3,'username3', 'email3','password3')");
//        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES (4,'username4', 'email4','password4')");
//        Paginated<User> userlist = userDao.findAll(Page.with(1, 10), new UserFilterBuilder().withSearch("user").build());
//        Assert.assertEquals(userlist.getTotalPages(), 1);
//        Assert.assertTrue(userlist.getList().contains(new User(ID,USERNAME,EMAIL, PASSWORD)));
//        Assert.assertTrue(userlist.getList().contains(new User(2L,"username2", "email2", "password2")));
//        Assert.assertTrue(userlist.getList().contains(new User(3L,"username3", "email3", "password3")));
//        Assert.assertTrue(userlist.getList().contains(new User(4L,"username4", "email4", "password4")));
//
//        Paginated<User> userlist2 = userDao.findAll(Page.with(1, 10), new UserFilterBuilder().withSearch("aaaaaaaa").build());
//        Assert.assertTrue(userlist2.getList().isEmpty());
//    }

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
}
