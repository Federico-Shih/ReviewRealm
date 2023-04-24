package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(String username, String email, String password);
    Optional<User> getByEmail(String email);
    Optional<User> getByUsername(String username);

    Optional<User> findById(final long id);
    void changePassword(String email, String password);
    void changeUsername(String email, String username);
}
