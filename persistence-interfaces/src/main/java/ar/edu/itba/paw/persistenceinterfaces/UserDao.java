package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.models.Follow;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    boolean exists(final long id);
    User create(String username, String email, String password);
    Optional<User> getByEmail(String email);
    Optional<User> getByUsername(String username);
    Optional<User> findById(final long id);
    void changePassword(String email, String password);
    void changeUsername(String email, String username);

    List<User> getFollowers(final long id);
    List<User> getFollowing(final long id);

    Optional<Follow> createFollow(final long userId, final long id);

    boolean deleteFollow(final long userId, final long id);

    boolean follows(final long userId, final long id);
}
