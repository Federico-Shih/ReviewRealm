package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Repository
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final SimpleJdbcInsert followJdbcInsert;

    private final static RowMapper<User> ROW_MAPPER = (resultSet, i) -> new User(resultSet.getLong("id"),
            resultSet.getString("username"),
            resultSet.getString("email"),
            resultSet.getString("password"));

    private final static RowMapper<Follow> FOLLOW_ROW_MAPPER = ((resultSet, i) -> new Follow(resultSet.getLong("userId"), resultSet.getLong("following")));

    private final static RowMapper<Role> ROLE_ROW_MAPPER = (((resultSet, i) -> new Role(resultSet.getString("roleName"))));
    @Autowired
    public UserDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds).
                withTableName("users").usingGeneratedKeyColumns("id");
        this.followJdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("followers").usingGeneratedKeyColumns("id");
    }

    @Override
    public boolean exists(final long id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id = ?", ROW_MAPPER, id).size() == 1;
    }

    @Override
    public Optional<User> findById(final long id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id = ?", ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public User create(String username, String email, String password) {
        final Map<String, Object> args = new HashMap<>();
        args.put("username", username);
        args.put("email",email);
        args.put("password",password);

        final Number id = jdbcInsert.executeAndReturnKey(args);
        return new User(id.longValue(), username, email, password);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return jdbcTemplate.query("SELECT * FROM users WHERE email = ?", ROW_MAPPER, email).stream().findFirst();
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return jdbcTemplate.query("SELECT * FROM users WHERE username = ?", ROW_MAPPER, username).stream().findFirst();
    }

    @Override
    public void changePassword(String email, String password) {
        jdbcTemplate.update("UPDATE users SET password = ? WHERE email = ?", password, email);
    }

    @Override
    public List<User> getFollowers(final long id) {
        return jdbcTemplate.query("SELECT u.username, u.id, u.email, u.password " +
                "FROM followers as f JOIN users as u ON f.userId = u.id " +
                "WHERE following = ?",
                ROW_MAPPER,
                id);
    }

    @Override
    public List<User> getFollowing(long id) {
        return jdbcTemplate.query("SELECT u.username, u.id, u.email, u.password " +
                "FROM followers f JOIN users u ON f.following = u.id " +
                "WHERE userId = ?",
                ROW_MAPPER,
                id);
    }

    @Override
    public Optional<Follow> createFollow(long userId, long id) {
        final Map<String, Object> args = new HashMap<>();
        args.put("userId", userId);
        args.put("following", id);
        followJdbcInsert.executeAndReturnKey(args);
        return Optional.of(new Follow(userId, id));
    }

    @Override
    public boolean deleteFollow(long userId, long id) {
        return jdbcTemplate.update("DELETE FROM followers WHERE userId = ? AND following = ?", userId, id) == 1;
    }

    @Override
    public boolean follows(long userId, long id) {
        return jdbcTemplate.query("SELECT * FROM followers WHERE userId = ? AND following = ?", FOLLOW_ROW_MAPPER,  userId, id).size() != 0;
    }

    @Override
    public List<Role> getRoles(long id) {
        return jdbcTemplate.query("SELECT * FROM user_roles NATURAL JOIN roles WHERE userId = ?", ROLE_ROW_MAPPER, id);
    }

    @Override
    public void changeUsername(String email, String username) {
        jdbcTemplate.update("UPDATE users SET username = ? WHERE email = ?", username, email);
    }

}
