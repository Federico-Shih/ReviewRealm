package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.dtos.SaveUserDTO;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.helpers.UpdateBuilder;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
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

    private final static RowMapper<User> USER_ROW_MAPPER = (resultSet, i) -> new User(resultSet.getLong("id"),
            resultSet.getString("username"),
            resultSet.getString("email"),
            resultSet.getString("password"),
            new ArrayList<>(),
            resultSet.getBoolean("enabled"),
            resultSet.getLong("reputation"),
            new HashSet<>(),
            new HashSet<>());

    private final static ResultSetExtractor<List<User>> USER_MAPPER = (resultSet) -> {
      Map<Long, User> users = new TreeMap<>();
      while(resultSet.next()) {
          final Long id = resultSet.getLong("id");
          users.putIfAbsent(id, USER_ROW_MAPPER.mapRow(resultSet, resultSet.getRow()));
          final User user = users.get(id);
          final String roleName = resultSet.getString("roleName");
          if(roleName != null) {
              user.getRoles().add(new Role(roleName));
          }
          final String notificationName = resultSet.getString("notificationType");
          if (notificationName != null) {
              user.getDisabledNotifications().add(new DisabledNotification(notificationName));
          }
      }
      return new ArrayList<>(users.values());
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
        UpdateBuilder updateBuilder = new UpdateBuilder().set("username", saveUserDTO.getUsername())
                .set("email", saveUserDTO.getEmail())
                .set("password", saveUserDTO.getPassword())
                .set("enabled", saveUserDTO.isEnabled());
        return jdbcTemplate.update("UPDATE users " + updateBuilder.toQuery() + " WHERE id = ?", updateBuilder.getParameters().toArray());
    }

    @Override
    public Optional<User> findById(final long id) {
        return jdbcTemplate.query("SELECT * FROM users u " + toUserDataString() +" WHERE id = ?",
                USER_MAPPER, id).stream().findFirst();
    }

    @Override
    public User create(String username, String email, String password) {
        final Map<String, Object> args = new HashMap<>();
        args.put("username", username);
        args.put("email",email);
        args.put("password",password);
        args.put("reputation",0);

        final Number id = jdbcInsert.executeAndReturnKey(args);
        return new User(id.longValue(), username, email, password);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return jdbcTemplate.query("SELECT * FROM users u " + toUserDataString() + " WHERE email = ?", USER_MAPPER, email).stream().findFirst();
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return jdbcTemplate.query("SELECT * FROM users u " + toUserDataString() + " WHERE username = ?", USER_MAPPER, username).stream().findFirst();
    }

    @Override
    public List<User> getFollowers(final long id) {
        return jdbcTemplate.query("SELECT u.id, u.username, u.email, u.password, u.enabled, u.reputation, roleName, notificationType " +
                "FROM followers as f JOIN users as u ON f.userId = u.id " + toUserDataString() +
                "WHERE f.following = ?",
                USER_MAPPER,
                id);
    }

    @Override
    public List<User> getFollowing(long id) {
        return jdbcTemplate.query("SELECT u.id, u.username, u.email, u.password, u.enabled, u.reputation, roleName, notificationType " +
                "FROM followers as f JOIN users as u ON f.following = u.id " + toUserDataString() +
                "WHERE f.userid = ?",
                USER_MAPPER,
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
        List<User> users = jdbcTemplate.query("SELECT * FROM users u " + toUserDataString() + " WHERE username ILIKE ? LIMIT ? OFFSET ?", USER_MAPPER, "%" + search + "%", pageSize, offset);
        return new Paginated<>(page, pageSize, offset, users);
    }

    @Override
    public Long getTotalAmountOfUsers() {
        return jdbcTemplate.queryForObject("SELECT count(*) from users", Long.class);
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
        return jdbcTemplate.update("UPDATE users SET reputation = coalesce(reputation,0) + ? WHERE id = ?", reputation, id) == 1;
    }

    private String toUserDataString() {
        return " LEFT OUTER JOIN user_roles ON u.id = user_roles.userId " +
                "LEFT OUTER JOIN roles ON user_roles.roleId = roles.roleId " +
                "LEFT OUTER JOIN user_disabled_notifications ON u.id = user_disabled_notifications.userId " +
                "LEFT OUTER JOIN notifications ON user_disabled_notifications.notificationId = notifications.notificationId ";
    }
}
