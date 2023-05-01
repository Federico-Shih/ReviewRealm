package ar.edu.itba.paw.persistence.tests;

import ar.edu.itba.paw.persistence.GameDaoImpl;
import ar.edu.itba.paw.persistence.UserDaoImpl;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class GameDaoImplTest {

    private final static Long ID = 7L;

    private final static String GAMENAME = "Martian Attack";
    private final static String GAMEDESC = "Martians attack";

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GameDaoImpl gameDao;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"games");
    }

    @Test
    public void testFindById() {
     //   jdbcTemplate.execute("INSERT INTO games (id, name, description, developer, publisher, publishdate, imageid) " +
             //   "VALUES ("+ID+",'"+GAMENAME+"','" + GAMEDESC + " , DEVELOPER, PUBLISHER, 12/12/2000, 2')");
    }
}
