package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.EmailAlreadyExistsException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.exceptions.UsernameAlreadyExistsException;
import ar.edu.itba.paw.models.Follow;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.FollowerFollowingCount;
import ar.edu.itba.paw.models.Role;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final GenreService genreService;

    @Autowired
    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder, GenreService genreService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.genreService = genreService;
    }

    @Override
    public User createUser(String username, String email, String password) throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
        if(userDao.getByEmail(email).isPresent())
            throw new EmailAlreadyExistsException();

        if(userDao.getByUsername(username).isPresent())
            throw new UsernameAlreadyExistsException();
        LOGGER.info("Creating user: {}", email);
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
        Optional<User> user = userDao.findById(id);
        user.ifPresent(value -> value.setPreferences(getPreferences(id)));
        return user;
    }

    @Override
    public void changeUserPassword(String email, String password) {
        LOGGER.info("Changing password: {}", email);
        userDao.changePassword(email, passwordEncoder.encode(password));
    }

    @Override
    public List<User> getFollowers(Long id) {
        return userDao.getFollowers(id);
    }

    @Override
    public List<User> getFollowing(Long id) {
        return userDao.getFollowing(id);
    }

    @Override
    public FollowerFollowingCount getFollowerFollowingCount(Long id) {
        return userDao.getFollowerFollowingCount(id);
    }

    @Override
    public Optional<Follow> followUserById(Long userId, Long otherId) {
        if (!userDao.exists(userId)) {
            throw new UserNotFoundException("notfound.currentuser");
        }
        if (!userDao.exists(otherId)) {
            throw new UserNotFoundException("notfound.otheruser");
        }
        return userDao.createFollow(userId, otherId);
    }

    @Override
    public boolean unfollowUserById(Long userId, Long otherId) {
        return userDao.deleteFollow(userId, otherId);
    }

    @Override
    public boolean userFollowsId(Long userId, Long otherId) {
        return userDao.follows(userId, otherId);
    }

    @Override
    public List<Role> getUserRoles(Long userId) {
        return userDao.getRoles(userId);
    }

    @Override
    public List<Genre> getPreferences(long userId) {
        List<Genre> list = new ArrayList<>();
        Optional<Genre> genre;
        for(Integer genreId : userDao.getPreferencesById(userId)){
            genre = genreService.getGenreById(genreId);
            genre.ifPresent(list::add);
        }
        return list;
    }
    @Override
    public void setPreferences(List<Integer> genres, long userId){
        userDao.setPreferences(genres, userId);
    }
}
