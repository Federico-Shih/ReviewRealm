package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.UserFilter;
import ar.edu.itba.paw.dtos.saving.SaveUserDTO;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.FollowerFollowingCount;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserDao {
    boolean exists(final long id);
    int update(final long id, SaveUserDTO saveUserDTO);
    User create(String username, String email, String password);
    Optional<User> getByEmail(String email);
    Optional<User> getByUsername(String username);
    Paginated<User> findAll(Page page, UserFilter userFilter);
    Optional<User> findById(final long id);
    List<User> getFollowers(final long id);
    List<User> getFollowing(final long id);
    FollowerFollowingCount getFollowerFollowingCount(final long id);

    boolean createFollow(final long userId, final long id);

    boolean deleteFollow(final long userId, final long id);

    boolean follows(final long userId, final long id);

    Set<Genre> getPreferences(final long userId);

    void setPreferences(Set<Integer> genres, long userId);

    Long getTotalAmountOfUsers();

    void disableNotification(long userId, String notificationType);

    void enableNotification(long userId, String notificationType);

}
