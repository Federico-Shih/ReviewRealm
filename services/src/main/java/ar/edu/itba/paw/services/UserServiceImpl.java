package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.InvalidUserException;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(String username, String email, String password) throws InvalidUserException {
        InvalidUserException invalidUserException = new InvalidUserException();
        if(userDao.getByEmail(email).isPresent())
            invalidUserException.setEmailAlreadyExists();
        if(userDao.getByUsername(username).isPresent())
            invalidUserException.setUsernameAlreadyExists();
        if(invalidUserException.hasErrors())
            throw invalidUserException;
        return userDao.create(username, email, passwordEncoder.encode(password));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userDao.getByEmail(email);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userDao.getByUsername(username);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public void changeUserPassword(String email, String password) {
        userDao.changePassword(email, passwordEncoder.encode(password));
    }
}
