package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.EmailAlreadyExistsException;
import ar.edu.itba.paw.exceptions.UserAlreadyEnabled;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.exceptions.UsernameAlreadyExistsException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.persistenceinterfaces.ValidationTokenDao;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import ar.edu.itba.paw.servicesinterfaces.MailingService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final GenreService genreService;
    private final ValidationTokenDao tokenDao;
    private final MailingService mailingService;
    private final Environment env;

    private static final int EXPIRATION_TIME = 60 * 60 * 24; // 24hs

    @Autowired
    private MessageSource messageSource;

    @Autowired
    public UserServiceImpl(UserDao userDao,
                           PasswordEncoder passwordEncoder,
                           GenreService genreService,
                           ValidationTokenDao tokenDao,
                           MailingService mailingService,
                           Environment env
    ) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.genreService = genreService;
        this.tokenDao = tokenDao;
        this.mailingService = mailingService;
        this.env = env;
    }

    @Override
    public User createUser(String username, String email, String password) throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
        Optional<User> user = userDao.getByEmail(email);
        boolean isEmptyPassword = false;
        if (user.isPresent() && !(isEmptyPassword = user.get().getPassword().equals(""))) {
            throw new EmailAlreadyExistsException();
        }
        if(!isEmptyPassword && userDao.getByUsername(username).isPresent())
            throw new UsernameAlreadyExistsException();
        User createdUser;
        if (isEmptyPassword) {
            LOGGER.info("Migrating empty pass user: {}", email);
            createdUser = user.get();
        } else {
            LOGGER.info("Creating user: {}", email);
            createdUser = userDao.create(username, email, "");
        }

        ExpirationToken token = this.tokenDao.create(RandomStringUtils.randomAlphanumeric(16),
                createdUser.getId(),
                passwordEncoder.encode(password),
                LocalDateTime.now().plusSeconds(EXPIRATION_TIME));
        sendValidationTokenEmail(token, createdUser);
        return createdUser;
    }

    private void sendValidationTokenEmail(ExpirationToken token, User user) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("webBaseUrl", env.getProperty("mailing.weburl"));
        templateVariables.put("token", token.getToken());
        templateVariables.put("user", user.getUsername());

        Object[] stringArgs = {};
        String subject = messageSource.getMessage("email.validation.subject",
                stringArgs, LocaleContextHolder.getLocale());

        mailingService.sendEmail(user.getEmail(), subject, "validate", templateVariables);
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
    public boolean validateToken(String token) {
        Optional<ExpirationToken> expToken = tokenDao.getByToken(token);
        expToken.ifPresent((expirationToken) -> {
            User user = userDao.findById(expirationToken.getUserId()).orElseThrow(() -> new UserNotFoundException("illegal.state"));
            userDao.changePassword(user.getEmail(), expirationToken.getPassword());
        });
        return expToken.isPresent();
    }

    @Override
    public void resendToken(String email) throws UserAlreadyEnabled {
        User user = userDao.getByEmail(email).orElseThrow(() -> new UserNotFoundException("user.notfound"));
        if (user.isEnabled()) {
            throw new UserAlreadyEnabled();
        }
        ExpirationToken token = tokenDao.refresh(user.getId(), RandomStringUtils.randomAlphanumeric(16));
        sendValidationTokenEmail(token, user);
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
