package ar.edu.itba.paw.persistence.tests;

import ar.edu.itba.paw.models.ExpirationToken;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistenceinterfaces.ValidationTokenDao;
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

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class TokenDaoImplTest {

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert userTemplate;

    private SimpleJdbcInsert tokenTemplate;

    @Autowired
    private ValidationTokenDao tokenDao;

    private final String TOKEN = "token";
    private long userId;
    private final String PASSWORD = "password";
    private final LocalDateTime EXPIRATION = LocalDateTime.now();

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        tokenTemplate = new SimpleJdbcInsert(ds).withTableName("tokens");
        userTemplate = new SimpleJdbcInsert(ds).withTableName("users");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tokens");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");

        Map<String, Object> userParams = new HashMap<>();
        userParams.put("username", "username");
        userParams.put("password", PASSWORD);
        userParams.put("email", "email");
        userParams.put("id", 1L);
        userTemplate.execute(userParams);
        this.userId = 1L;
    }

    @Test
    public void createExpirationTokenTest() {
        ExpirationToken token = tokenDao.create(userId, PASSWORD, EXPIRATION);
        Assert.assertNotNull(token);
        Assert.assertEquals(userId, (long) token.getUser().getId());
        Assert.assertEquals(PASSWORD, token.getPassword());
        Assert.assertEquals(EXPIRATION, token.getExpiration());
    }

    @Test
    public void findLastPasswordTokenTest() {
        tokenTemplate.execute(new HashMap<String, Object>() {{
            put("token", TOKEN);
            put("userid", userId);
            put("password", PASSWORD);
            put("expiration", EXPIRATION);
        }});

        ExpirationToken token = tokenDao.findLastPasswordToken(userId).orElse(null);
        Assert.assertNotNull(token);
        Assert.assertEquals(TOKEN, token.getToken());
        Assert.assertEquals(userId, (long) token.getUser().getId());
        Assert.assertEquals(PASSWORD, token.getPassword());
        Assert.assertEquals(EXPIRATION, token.getExpiration());
    }

    @Test
    public void noLastTokenFoundTest() {
        ExpirationToken token = tokenDao.findLastPasswordToken(userId).orElse(null);
        Assert.assertNull(token);
    }

    // findLastPasswordToken only returns the last token with password creation
    @Test
    public void noValidateUserTokenTest() {
        tokenTemplate.execute(new HashMap<String, Object>() {{
            put("token", TOKEN);
            put("userid", userId);
            put("password", "");
            put("expiration", EXPIRATION);
        }});

        ExpirationToken token = tokenDao.findLastPasswordToken(userId).orElse(null);
        Assert.assertNull(token);
    }

    @Test
    public void deleteTokenByIdTest() {
        tokenTemplate.execute(new HashMap<String, Object>() {{
            put("token", TOKEN);
            put("userid", userId);
            put("password", PASSWORD);
            put("expiration", EXPIRATION);
        }});

        Assert.assertTrue(tokenDao.delete(TOKEN));
        Assert.assertFalse(tokenDao.delete(TOKEN));
    }

    @Test
    public void getExpirationTokenByTokenString() {
        tokenTemplate.execute(new HashMap<String, Object>() {{
            put("token", TOKEN);
            put("userid", userId);
            put("password", PASSWORD);
            put("expiration", EXPIRATION);
        }});

        Optional<ExpirationToken> token = tokenDao.getByToken(TOKEN);
        Assert.assertTrue(token.isPresent());
        Assert.assertEquals(TOKEN, token.get().getToken());
        Assert.assertEquals(userId, (long) token.get().getUser().getId());
        Assert.assertEquals(PASSWORD, token.get().getPassword());
        Assert.assertEquals(EXPIRATION, token.get().getExpiration());
    }

    @Test
    public void noTokenFoundByTokenString() {
        Optional<ExpirationToken> token = tokenDao.getByToken(TOKEN);
        Assert.assertFalse(token.isPresent());
    }
}
