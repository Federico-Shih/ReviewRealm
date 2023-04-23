package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(String email, String password);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserById(Long id);
}
