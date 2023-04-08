package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserDao {
    User create(String email, String password);
    Optional<User> getByEmail(String email);

    Optional<User> findById(final long id);
}
