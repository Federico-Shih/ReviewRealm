package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.exceptions.InvalidUserException;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(String username, String email, String password) throws InvalidUserException;
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserById(Long id);
    void changeUserPassword(String email, String password);
}
