package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.dtos.SaveUserDTO;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;


@Repository
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final SimpleJdbcInsert followJdbcInsert;

    private final static RowMapper<User> USER_ROW_MAPPER = (resultSet, i) -> {
        List<Genre> roles = new ArrayList<>();

        return new User(resultSet.getLong("id"),
                resultSet.getString("username"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                roles,
                resultSet.getBoolean("enabled"),
                resultSet.getLong("reputation"));
    };
    private final static RowMapper<Follow> FOLLOW_ROW_MAPPER = ((resultSet, i) -> new Follow(resultSet.getLong("userId"), resultSet.getLong("following")));

    private final static RowMapper<Role> ROLE_ROW_MAPPER = (((resultSet, i) -> new Role(resultSet.getString("roleName"))));

    private final static RowMapper<DisabledNotification> DISABLED_NOTIFICATION_ROW_MAPPER = ((resultSet, i) -> new DisabledNotification(resultSet.getString("notificationType")));

    private final static RowMapper<FollowerFollowingCount> FOLLOWER_FOLLOWING_COUNT_ROW_MAPPER = (((resultSet, i) -> new FollowerFollowingCount(resultSet.getLong("follower_count"), resultSet.getLong("following_count"))));

    private final static RowMapper<Integer> GENRE_ROW_MAPPER = ((resultSet, i) -> resultSet.getInt("genreId"));

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
        return jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, id).size() == 1;
    }

    @Override
    public int update(long id, SaveUserDTO saveUserDTO) {
        List<Object> args = new ArrayList<>();
        String updateString = toUpdateString(saveUserDTO, args);
        args.add(id);
        return jdbcTemplate.update("UPDATE users " + updateString + " WHERE id = ?", args.toArray());
    }

    private String toUpdateString(SaveUserDTO updateUser, List<Object> args) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SET ");
        boolean first = true;
        if (updateUser.getUsername() != null) {
            if (!first) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("username = ?");
            args.add(updateUser.getUsername());
            first = false;
        }
        if (updateUser.getEmail() != null) {
            if (!first) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("email = ?");
            args.add(updateUser.getEmail());
            first = false;
        }
        if (updateUser.getPassword() != null) {
            if (!first) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("password = ?");
            args.add(updateUser.getPassword());
            first = false;
        }
        if (updateUser.isEnabled() != null) {
            if (!first) {
                stringBuilder.append(", ");
            }
            stringBuilder.append("enabled = ?");
            args.add(updateUser.isEnabled());
        }
        stringBuilder.append(" ");
        return stringBuilder.toString();
    }

    @Override
    public Optional<User> findById(final long id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id = ?", USER_ROW_MAPPER, id).stream().findFirst();
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
        return jdbcTemplate.query("SELECT * FROM users WHERE email = ?", USER_ROW_MAPPER, email).stream().findFirst();
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return jdbcTemplate.query("SELECT * FROM users WHERE username = ?", USER_ROW_MAPPER, username).stream().findFirst();
    }

    @Override
    public List<User> getFollowers(final long id) {
        return jdbcTemplate.query("SELECT u.id, u.username, u.email, u.password, u.enabled " +
                "FROM followers as f JOIN users as u ON f.userId = u.id " +
                "WHERE f.following = ?",
                USER_ROW_MAPPER,
                id);
    }

    @Override
    public List<User> getFollowing(long id) {
        return jdbcTemplate.query("SELECT u.id, u.username, u.email, u.password, u.enabled " +
                "FROM followers as f JOIN users as u ON f.following = u.id " +
                "WHERE userId = ?",
                USER_ROW_MAPPER,
                id);
    }

    @Override
    public FollowerFollowingCount getFollowerFollowingCount(final long id) {
        return jdbcTemplate.query("SELECT count(*) as following_count, " +
                        "(SELECT count(*) FROM followers WHERE following = ?) as follower_count " +
                        "FROM followers WHERE userid = ?",
                FOLLOWER_FOLLOWING_COUNT_ROW_MAPPER,
                id, id).get(0);
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
    public List<Integer> getPreferencesById(long userId){
        return jdbcTemplate.query("SELECT genreId FROM genreforusers WHERE userId = ?", GENRE_ROW_MAPPER, userId);
    }

    @Override
    public void setPreferences(List<Integer> genres, long userId) {
        jdbcTemplate.update("DELETE FROM genreforusers WHERE userid = ?",userId);
        // Si tuvieramos muchos géneros a la vez (10 o más), deberíamos utilizar batchUpdate
        for(Integer genId : genres) {
            jdbcTemplate.update("INSERT INTO genreforusers(genreid, userid) VALUES (?,?)", genId, userId);
        }
    }
    @Override
    public Paginated<User> getSearchedUsers(int page, int pageSize, int offset, String search) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE username ILIKE ? LIMIT ? OFFSET ?", USER_ROW_MAPPER, "%" + search + "%", pageSize, offset);
        return new Paginated<>(page, pageSize, offset, users);
    }

    @Override
    public Long getTotalAmountOfUsers() {
        return jdbcTemplate.queryForObject("SELECT count(*) from users", Long.class);
    }

    @Override
    public List<DisabledNotification> getDisabledNotifications(long userId) {
        return jdbcTemplate.query("SELECT * FROM user_disabled_notifications NATURAL JOIN notifications WHERE userId = ?", DISABLED_NOTIFICATION_ROW_MAPPER, userId);
    }

    @Override
    public void disableNotification(long userId, String notificationType) {
        jdbcTemplate.update("WITH notification_id(id) AS (SELECT notificationId FROM notifications WHERE notificationType = ?) INSERT INTO user_disabled_notifications(userId, notificationId) SELECT ?, id FROM notification_id WHERE NOT EXISTS (SELECT * FROM user_disabled_notifications WHERE userId = ? AND notificationId = notification_id.id)", notificationType, userId, userId);
    }

    @Override
    public void enableNotification(long userId, String notificationType) {
        jdbcTemplate.update("DELETE FROM user_disabled_notifications WHERE userId = ? AND notificationId = (SELECT notificationId FROM notifications WHERE notificationType = ? LIMIT 1)", userId, notificationType);
    }

    @Override
    public boolean modifyReputation(long id, int reputation) {
        return jdbcTemplate.update("UPDATE users SET reputation = reputation + ? WHERE id = ?", reputation, id) == 1;
    }
}
