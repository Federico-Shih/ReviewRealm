package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.saving.SaveUserDTO;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.dtos.filtering.UserFilter;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.helpers.CommonRowMappers;
import ar.edu.itba.paw.persistence.helpers.QueryBuilder;
import ar.edu.itba.paw.persistence.helpers.UpdateBuilder;
import ar.edu.itba.paw.persistenceinterfaces.PaginationDao;
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
public class UserDaoImpl implements UserDao, PaginationDao<UserFilter> {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final SimpleJdbcInsert followJdbcInsert;

    private final static RowMapper<User> USER_ROW_MAPPER = (resultSet, i) -> new User(resultSet.getLong("id"),
            resultSet.getString("username"),
            resultSet.getString("email"),
            resultSet.getString("password"),
            new HashSet<>(),
            resultSet.getBoolean("enabled"),
            resultSet.getLong("reputation"),
            new HashSet<>(),
            new HashSet<>(),
            resultSet.getLong("avatar"));
    private final static RowMapper<Follow> FOLLOW_ROW_MAPPER = ((resultSet, i) -> new Follow(resultSet.getLong("userId"), resultSet.getLong("following")));

    private final static RowMapper<Role> ROLE_ROW_MAPPER = (((resultSet, i) -> new Role(resultSet.getString("roleName"))));
    private final static RowMapper<DisabledNotification> DISABLED_NOTIFICATION_ROW_MAPPER = ((resultSet, i) -> new DisabledNotification(resultSet.getString("notificationType")));
    private final static RowMapper<Integer> GENRE_ROW_MAPPER = ((resultSet, i) -> resultSet.getInt("genreId"));

    private final static ResultSetExtractor<List<User>> USER_MAPPER = (resultSet) -> {
        Map<Long, User> users = new LinkedHashMap<>();
        while(resultSet.next()) {
            final Long id = resultSet.getLong("id");
            users.putIfAbsent(id, USER_ROW_MAPPER.mapRow(resultSet, resultSet.getRow()));
            final User user = users.get(id);
            final String roleName = resultSet.getString("roleName");
            if(roleName != null) {
                user.getRoles().add(ROLE_ROW_MAPPER.mapRow(resultSet, resultSet.getRow()));
            }
            final String notificationName = resultSet.getString("notificationType");
            if (notificationName != null) {
                user.getDisabledNotifications().add(DISABLED_NOTIFICATION_ROW_MAPPER.mapRow(resultSet, resultSet.getRow()));
            }
            if (resultSet.getInt("genreId") != 0) {
                user.getPreferences().add(Genre.getById(GENRE_ROW_MAPPER.mapRow(resultSet, resultSet.getRow())).get());
            }
        }
        return new ArrayList<>(users.values());
    };


    private final static RowMapper<FollowerFollowingCount> FOLLOWER_FOLLOWING_COUNT_ROW_MAPPER = (((resultSet, i) -> new FollowerFollowingCount(resultSet.getLong("follower_count"), resultSet.getLong("following_count"))));


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
                .set("enabled", saveUserDTO.isEnabled())
                .set("reputation", saveUserDTO.getReputation())
                .set("avatar", saveUserDTO.getAvatar());

        updateBuilder.getParameters().add(id);
        return jdbcTemplate.update("UPDATE users " + updateBuilder.toQuery() + " WHERE id = ?", updateBuilder.getParameters().toArray());
    }

    @Override
    public Optional<User> findById(final long id) {
        return jdbcTemplate.query("SELECT * FROM users u " + toUserDataString() + " WHERE id = ?",
                USER_MAPPER, id).stream().findFirst();
    }

    @Override
    public User create(String username, String email, String password) {
        final Map<String, Object> args = new HashMap<>();
        args.put("username", username);
        args.put("email",email);
        args.put("password",password);
        args.put("reputation",0);
        args.put("avatar",0);

        final Number id = jdbcInsert.executeAndReturnKey(args);
        return new User(id.longValue(), username, email, password);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return jdbcTemplate.query("SELECT * FROM users u " + toUserDataString() + " WHERE email = ?", USER_MAPPER, email).stream().findFirst();
    }

    @Override
    public Paginated<User> findAll(Page page, UserFilter userFilter) {
        int totalPages = getPageCount(userFilter, page.getPageSize());
        if (page.getPageNumber() > totalPages || page.getPageNumber() < 1) {
            return new Paginated<>(page.getPageNumber(), page.getPageSize(), totalPages, Collections.emptyList());
        }
        QueryBuilder queryBuilder = new QueryBuilder()
                .withExact("u.id", userFilter.getId())
                .withExact("u.username", userFilter.getUsername())
                .withExact("u.email", userFilter.getEmail())
                .withExact("u.enabled", userFilter.isEnabled())
                .withExact("u.reputation", userFilter.getReputation())
                .withSimilar("u.username", userFilter.getSearch());
        queryBuilder.toArguments().add(page.getPageSize());
        queryBuilder.toArguments().add(page.getOffset());
        List<User> users = jdbcTemplate.query("SELECT * FROM users u " + queryBuilder.toQuery() + " LIMIT ? OFFSET ? ", USER_ROW_MAPPER, queryBuilder.toArguments().toArray());
        return new Paginated<>(page.getPageNumber(), page.getPageSize(), totalPages, users);
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return jdbcTemplate.query("SELECT * FROM users u " + toUserDataString() + " WHERE username = ?", USER_MAPPER, username).stream().findFirst();
    }

    @Override
    public List<User> getFollowers(final long id) {
        return jdbcTemplate.query("SELECT distinct u.id, u.username, u.email, u.password, u.enabled, u.reputation,u.avatar " +
                "FROM followers as f JOIN users as u ON f.userId = u.id " +
                "WHERE f.following = ?",
                USER_ROW_MAPPER,
                id);
    }

    @Override
    public List<User> getFollowing(long id) {
        return jdbcTemplate.query("SELECT distinct u.id, u.username, u.email, u.password, u.enabled, u.reputation, u.avatar " +
                "FROM followers as f JOIN users as u ON f.following = u.id " +
                "WHERE f.userid = ?",
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
    public Set<Genre> getPreferences(long userId){
        return new HashSet<>(jdbcTemplate.query("SELECT genreId FROM genreforusers WHERE userId = ?", CommonRowMappers.GENRE_ROW_MAPPER, userId));
    }

    @Override
    public void setPreferences(Set<Integer> genres, long userId) {
        jdbcTemplate.update("DELETE FROM genreforusers WHERE userid = ?",userId);
        // Si tuvieramos muchos géneros a la vez (10 o más), deberíamos utilizar batchUpdate
        for(Integer genId : genres) {
            jdbcTemplate.update("INSERT INTO genreforusers(genreid, userid) VALUES (?,?)", genId, userId);
        }
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


    private String toUserDataString() {
        return " LEFT OUTER JOIN user_roles ON u.id = user_roles.userId " +
                "LEFT OUTER JOIN roles ON user_roles.roleId = roles.roleId " +
                "LEFT OUTER JOIN user_disabled_notifications ON u.id = user_disabled_notifications.userId " +
                "LEFT OUTER JOIN notifications ON user_disabled_notifications.notificationId = notifications.notificationId " +
                "LEFT OUTER JOIN genreforusers ON u.id = genreforusers.userId ";
    }

    @Override
    public Long count(UserFilter filter) {
        QueryBuilder queryBuilder = new QueryBuilder()
                .withExact("u.id", filter.getId())
                .withExact("u.username", filter.getUsername())
                .withExact("u.email", filter.getEmail())
                .withExact("u.enabled", filter.isEnabled())
                .withExact("u.reputation", filter.getReputation())
                .withSimilar("u.username", filter.getSearch());
        return jdbcTemplate.queryForObject("SELECT count(*) FROM users u " + queryBuilder.toQuery(), Long.class, queryBuilder.toArguments().toArray());
    }
}
