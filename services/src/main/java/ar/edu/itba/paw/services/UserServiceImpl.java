package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.UserFilterBuilder;
import ar.edu.itba.paw.enums.NotificationType;
import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.dtos.saving.SaveUserBuilder;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private static final int EXPIRATION_TIME = 60 * 60 * 24; // 24hs

    @Autowired
    public UserServiceImpl(UserDao userDao,
                           PasswordEncoder passwordEncoder,
                           GenreService genreService,
                           ValidationTokenDao tokenDao,
                           MailingService mailingService
    ) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.genreService = genreService;
        this.tokenDao = tokenDao;
        this.mailingService = mailingService;
    }

    @Transactional
    @Override
    public User createUser(String username, String email, String password) throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
        Optional<User> user = userDao.getByEmail(email);
        if (user.isPresent()) {
            throw new EmailAlreadyExistsException();
        }
        if(userDao.getByUsername(username).isPresent())
            throw new UsernameAlreadyExistsException();
        User createdUser;
        LOGGER.info("Creating user: {}", email);
        createdUser = userDao.create(username, email, "");

        ExpirationToken token = this.tokenDao.create(
                generateToken(),
                createdUser.getId(),
                passwordEncoder.encode(password),
                LocalDateTime.now().plusSeconds(EXPIRATION_TIME));
        mailingService.sendValidationTokenEmail(token, createdUser);
        return createdUser;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUserByEmail(String email) {
        return userDao.getByEmail(email);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUserByUsername(String username) {
        return userDao.getByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUserByToken(String token) {
        ExpirationToken expirationToken = tokenDao.getByToken(token).orElseThrow(() -> new UserNotFoundException("user.notfound"));
        return getUserById(expirationToken.getUserId());
    }

    @Transactional(readOnly = true)
    @Override
    public void changeUserPassword(String email, String password) {
        LOGGER.info("Changing password: {}", email);
        User user = getUserByEmail(email).orElseThrow(() -> new UserNotFoundException("notfound.user"));
        SaveUserBuilder saveUserBuilder = new SaveUserBuilder().withPassword(passwordEncoder.encode(password));
        userDao.update(user.getId(), saveUserBuilder.build());
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getFollowers(Long id) {
        return userDao.getFollowers(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getFollowing(Long id) {
        return userDao.getFollowing(id);
    }

    @Transactional(readOnly = true)
    @Override
    public FollowerFollowingCount getFollowerFollowingCount(Long id) {
        return userDao.getFollowerFollowingCount(id);
    }

    @Transactional
    @Override
    public Optional<Follow> followUserById(Long userId, Long otherId) {
        if (!userDao.exists(userId)) {
            throw new UserNotFoundException("notfound.currentuser");
        }
        if (!userDao.exists(otherId)) {
            throw new UserNotFoundException("notfound.otheruser");
        }
        LOGGER.info("User {} followed user {}", userId, otherId);
        return userDao.createFollow(userId, otherId);
    }

    @Transactional
    @Override
    public boolean unfollowUserById(Long userId, Long otherId) {
        boolean op = userDao.deleteFollow(userId, otherId);
        LOGGER.info("User {} unfollowed user {}", userId, otherId);
        return op;
    }

    @Transactional
    @Override
    public boolean userFollowsId(Long userId, Long otherId) {
        return userDao.follows(userId, otherId);
    }

    @Transactional
    @Override
    public Optional<User> validateToken(String token) throws TokenExpiredException {
        Optional<ExpirationToken> expToken = tokenDao.getByToken(token);
        if (expToken.isPresent() && expToken.get().getExpiration().isBefore(LocalDateTime.now())) {
            LOGGER.error("Token for User {} expired", expToken.get().getUserId());
            throw new TokenExpiredException("token.expired");
        }
        if (!expToken.isPresent()) {
            return Optional.empty();
        }
        ExpirationToken expirationToken = expToken.get();
        User user = userDao.findById(expirationToken.getUserId()).orElseThrow(() -> new UserNotFoundException("illegal.state"));
        SaveUserBuilder userBuilder = new SaveUserBuilder().withEnabled(true).withPassword(expirationToken.getPassword());
        userDao.update(user.getId(), userBuilder.build());
        tokenDao.delete(expirationToken.getId());
        LOGGER.info("User {} validated token", user.getId());
        return Optional.of(user);
    }

    @Transactional
    @Override
    public void resendToken(String email) throws UserAlreadyEnabled {
        User user = getUserByEmail(email).orElseThrow(() -> new UserNotFoundException("user.notfound"));
        if (user.isEnabled()) {
            LOGGER.error("User {} already enabled", user.getId());
            throw new UserAlreadyEnabled();
        }
        ExpirationToken token = tokenDao.findLastPasswordToken(user.getId()).orElseThrow(() -> new UserNotFoundException("user.notfound"));
        ExpirationToken newToken = tokenDao.create(generateToken(), user.getId(), token.getPassword(), LocalDateTime.now().plusHours(EXPIRATION_TIME));
        tokenDao.delete(token.getId());
        mailingService.sendValidationTokenEmail(newToken, user);
        LOGGER.info("User {} resent token", user.getId());
    }

    @Transactional
    @Override
    public void refreshToken(String token) {
        ExpirationToken expirationToken = tokenDao.getByToken(token).orElseThrow(() -> new UserNotFoundException("user.notfound"));
        User user = getUserById(expirationToken.getUserId()).orElseThrow(() -> new UserNotFoundException("user.notfound"));
        ExpirationToken newToken = tokenDao.create(generateToken(), user.getId(), expirationToken.getPassword(), LocalDateTime.now().plusHours(EXPIRATION_TIME));
        if (newToken == null) {
            LOGGER.error("User {} not found when refreshing token", user.getId());
            throw new UserNotFoundException("user.not.found");
        }
        tokenDao.delete(expirationToken.getId());
        mailingService.sendValidationTokenEmail(newToken, user);
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<User> getSearchedUsers(int page, int pageSize, String search) {
        return userDao.findAll(Page.with(page, pageSize), new UserFilterBuilder().withSearch(search).build());
    }

    @Transactional
    @Override
    public void sendPasswordResetToken(String email) throws UserNotFoundException {
        User user = getUserByEmail(email).orElseThrow(() -> new UserNotFoundException("user.notfound"));
        ExpirationToken newToken = tokenDao.create(generateToken(), user.getId(), "", LocalDateTime.now().plusHours(EXPIRATION_TIME));
        mailingService.sendChangePasswordEmail(newToken, user);
        LOGGER.info("Sending User {} a password reset token", user.getId());
    }

    @Transactional
    @Override
    public boolean resetPassword(String token, String password) throws TokenExpiredException, TokenNotFoundException {
        ExpirationToken existentToken = tokenDao.getByToken(token).orElseThrow(() -> new TokenNotFoundException("token.notfound"));
        if (existentToken.getExpiration().isBefore(LocalDateTime.now())) {
            LOGGER.error("Token for User {} password reset expired", existentToken.getUserId());
            throw new TokenExpiredException("token.expired");
        }
        boolean op = userDao.update(existentToken.getUserId(), new SaveUserBuilder().withPassword(passwordEncoder.encode(password)).withEnabled(true).build()) == 1;
        LOGGER.info("User {} reset password", existentToken.getUserId());
        return op;
    }

    @Transactional(readOnly = true)
    @Override
    public Map<NotificationType, Boolean> getUserNotificationSettings(Long userId) {
        Map<NotificationType, Boolean> notificationSettings = new HashMap<>();
        Set<DisabledNotification> disabledNotifications = getUserById(userId).orElseThrow(() -> new UserNotFoundException("user.notfound")).getDisabledNotifications();
        for (DisabledNotification disabledNotification : disabledNotifications) {
            notificationSettings.put(disabledNotification.getNotificationType(), false);
        }
        for (NotificationType notificationType : NotificationType.values()) {
            if (!notificationSettings.containsKey(notificationType)) {
                notificationSettings.put(notificationType, true);
            }
        }
        return notificationSettings;
    }

    @Transactional(readOnly = true)
    @Override
    public Boolean isNotificationEnabled(Long userId, NotificationType notificationType) {
        Set<DisabledNotification> disabledNotifications = getUserById(userId).orElseThrow(() -> new UserNotFoundException("user.notfound")).getDisabledNotifications();
        return disabledNotifications.stream().noneMatch(disabledNotification -> disabledNotification.getNotificationType().equals(notificationType));
    }

    @Transactional
    @Override
    public void setUserNotificationSettings(Long userId, Map<NotificationType, Boolean> notificationSettings) {
        Map<NotificationType, Boolean> currentNotificationSettings = getUserNotificationSettings(userId);
        for (Map.Entry<NotificationType, Boolean> entry : notificationSettings.entrySet()) {
            if (entry.getValue() && !currentNotificationSettings.get(entry.getKey())) {
                userDao.enableNotification(userId, entry.getKey().getTypeName());
                LOGGER.info("User {} enabled notification {}", userId, entry.getKey().getTypeName());
            } else if (!entry.getValue() && currentNotificationSettings.get(entry.getKey())) {
                userDao.disableNotification(userId, entry.getKey().getTypeName());
                LOGGER.info("User {} disabled notification {}", userId, entry.getKey().getTypeName());
            }
        }
    }

    @Override
    public void setPreferences(Set<Integer> genres, long userId){
        LOGGER.info("User {} set genre preferences", userId);
        userDao.setPreferences(genres, userId);
    }

    @Transactional
    @Override
    public boolean modifyUserReputation(long id, int reputation) {
        User user = userDao.findById(id).orElseThrow(() -> new UserNotFoundException("user.notfound"));
        SaveUserBuilder builder = new SaveUserBuilder().withReputation(user.getReputation() + reputation);
        return userDao.update(id, builder.build()) == 1;
    }

    @Transactional
    @Override
    public void changeUserAvatar(long userId, long imageId) throws InvalidAvatarException {
        if(imageId>6 || imageId<1)
            throw new InvalidAvatarException(imageId);
        SaveUserBuilder builder = new SaveUserBuilder().withAvatar(imageId);
        userDao.update(userId, builder.build());
        LOGGER.info("User {} changed avatar to {}", userId, imageId);
    }

    private String generateToken() {
        return RandomStringUtils.randomAlphanumeric(16);
    }
}
