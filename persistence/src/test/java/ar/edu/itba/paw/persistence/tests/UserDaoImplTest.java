package ar.edu.itba.paw.persistence.tests;

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
import org.springframework.test.context.jdbc.Sql;
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
