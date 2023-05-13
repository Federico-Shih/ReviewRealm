package ar.edu.itba.paw.persistence.tests;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.builders.SaveUserBuilder;
import ar.edu.itba.paw.dtos.builders.UserFilterBuilder;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.UserDaoImpl;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistenceinterfaces.ValidationTokenDao;
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
import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class TokenDaoImplTest {

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;


    @Autowired
    private ValidationTokenDao tokenDao;

    private final String TOKEN = "token";
    private final long USER_ID = 1L;
    private final String PASSWORD = "password";
    private final LocalDateTime EXPIRATION = LocalDateTime.now();

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"tokens");
    }


//    @Test
//    public void testFindById() throws SQLException{
//        //1.prepare
//        jdbcTemplate.execute("INSERT INTO users (id,username,email,password) VALUES ("+ID+",'"+USERNAME+"', '"+EMAIL+"','"+PASSWORD+"')");
//        //2.execute
//        Optional<User> maybeUser = userDao.findById(ID);
//
//        //3.assert
//        Assert.assertTrue(maybeUser.isPresent());
//        Assert.assertEquals(ID,maybeUser.get().getId());
//        Assert.assertEquals(EMAIL,maybeUser.get().getEmail());
//        Assert.assertEquals(PASSWORD,maybeUser.get().getPassword());
//    }
}
