package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.models.User;

public interface UserDao {
    User create(String email, String password);
}
