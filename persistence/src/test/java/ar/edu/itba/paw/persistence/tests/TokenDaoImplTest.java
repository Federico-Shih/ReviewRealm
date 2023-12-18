package ar.edu.itba.paw.persistence.tests;

import ar.edu.itba.paw.models.ExpirationToken;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.tests.utils.TokenTestModels;
import ar.edu.itba.paw.persistenceinterfaces.ValidationTokenDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @Autowired
    private ValidationTokenDao tokenDao;
    private ExpirationToken createToken;

    @PersistenceContext
    private EntityManager em;

    private ExpirationToken passwordToken;

    private final static String INEXISTENT_TOKEN = "aaaa";

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        createToken = TokenTestModels.getCreateToken();
        passwordToken = TokenTestModels.getToken2();
    }

    @Rollback
    @Test
    public void createExpirationTokenTest() {
        ExpirationToken token = tokenDao.create(createToken.getUser().getId(), createToken.getExpiration());
        Assert.assertNotNull(token);
        em.flush();

        int count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "tokens", "token = '" + token.getToken() + "'");
        Assert.assertEquals(1, count);
    }

    @Rollback
    @Test
    public void findLastPasswordTokenTest() {
        ExpirationToken token = tokenDao.findLastPasswordToken(passwordToken.getUser().getId()).orElse(null);
        Assert.assertNotNull(token);
        Assert.assertEquals(passwordToken.getToken(), token.getToken());
        Assert.assertEquals(passwordToken.getUser(), token.getUser());
        Assert.assertEquals(passwordToken.getPassword(), token.getPassword());
        Assert.assertEquals(passwordToken.getExpiration(), token.getExpiration());
    }

    @Rollback
    @Test
    public void noLastTokenFoundTest() {
        ExpirationToken token = tokenDao.findLastPasswordToken(-1L).orElse(null);
        Assert.assertNull(token);
    }

    // findLastPasswordToken only returns the last token with password creation
    @Rollback
    @Test
    public void noValidateUserTokenTest() {
        ExpirationToken token = tokenDao.findLastPasswordToken(TokenTestModels.getToken1().getUser().getId()).orElse(null);
        Assert.assertNull(token);
    }

    @Rollback
    @Test
    public void deleteTokenByIdTest() {
        Assert.assertTrue(tokenDao.delete(passwordToken.getToken()));
        Assert.assertFalse(tokenDao.delete(passwordToken.getToken()));
        em.flush();
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "tokens", "token = '" + passwordToken.getToken() + "'"));
    }

    @Rollback
    @Test
    public void getExpirationTokenByTokenString() {
        Optional<ExpirationToken> token = tokenDao.getByToken(passwordToken.getToken());
        Assert.assertTrue(token.isPresent());
    }

    @Rollback
    @Test
    public void noTokenFoundByTokenString() {
        Optional<ExpirationToken> token = tokenDao.getByToken(INEXISTENT_TOKEN);
        Assert.assertFalse(token.isPresent());
    }
}
