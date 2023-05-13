package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.ExpirationToken;
import ar.edu.itba.paw.persistenceinterfaces.ValidationTokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ValidationTokenDaoImpl implements ValidationTokenDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsertTemplate;

    @Autowired
    public ValidationTokenDaoImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsertTemplate = new SimpleJdbcInsert(dataSource)
                .withTableName("tokens")
                .usingGeneratedKeyColumns("id");
    }

    private final RowMapper<ExpirationToken> ROW_MAPPER = ((resultSet, i) -> (
            new ExpirationToken(
            resultSet.getLong("id"),
                    resultSet.getString("token"),
                    resultSet.getLong("userid"),
                    resultSet.getString("password"),
                    resultSet.getTimestamp("expiration").toLocalDateTime()
            )
    )
    );

    @Override
    public ExpirationToken create(String token, long userId, String password, LocalDateTime expiration) {
        Map<String, Object> args = new HashMap<>();
        args.put("token", token);
        args.put("userid", userId);
        args.put("expiration", Timestamp.valueOf(expiration));
        args.put("password", password);
        Number id = jdbcInsertTemplate.executeAndReturnKey(args);
        return new ExpirationToken(id.longValue(), token, userId, password, expiration);
    }

    @Override
    public Optional<ExpirationToken> findLastPasswordToken(long userId) {
        return jdbcTemplate
                .query(
                        "SELECT * FROM tokens WHERE userid = ? AND password <> '' ORDER BY expiration DESC",
                        ROW_MAPPER,
                        userId)
                .stream()
                .findFirst();
    }

    @Override
    public boolean delete(Long id) {
        return jdbcTemplate.update("DELETE FROM tokens WHERE id = ?", id) > 0;
    }

    @Override
    public Optional<ExpirationToken> getByToken(String token) {
        return jdbcTemplate.query("SELECT * FROM tokens WHERE token = ?", ROW_MAPPER, token).stream().findFirst();
    }
}
