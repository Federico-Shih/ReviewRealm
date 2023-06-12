package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.UserFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.UserOrderCriteria;
import ar.edu.itba.paw.dtos.saving.SaveUserBuilder;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Mission;
import ar.edu.itba.paw.enums.NotificationType;
import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import ar.edu.itba.paw.persistenceinterfaces.ValidationTokenDao;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.MailingService;
import ar.edu.itba.paw.servicesinterfaces.MissionService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final ValidationTokenDao tokenDao;
    private final MailingService mailingService;

    private final GameService gameService;

    private final MissionService missionService;
    private static final int EXPIRATION_TIME = 60 * 60 * 24; // 24hs
    private static final int AVATAR_AMOUNT = 6;

    @Autowired
    public UserServiceImpl(UserDao userDao,
                           PasswordEncoder passwordEncoder,
                           ValidationTokenDao tokenDao,
                           MailingService mailingService,
                           GameService gameService, MissionService missionService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.tokenDao = tokenDao;
        this.mailingService = mailingService;
        this.gameService = gameService;
        this.missionService = missionService;
    }

    @Transactional
    @Override
    public User createUser(String username, String email, String password, Locale locale) throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
        Optional<User> user = userDao.getByEmail(email);
        if (user.isPresent()) {
            throw new EmailAlreadyExistsException();
        }
        if(userDao.getByUsername(username).isPresent())
            throw new UsernameAlreadyExistsException();
        User createdUser;
        LOGGER.info("Creating user: {}", email);
        createdUser = userDao.create(username, email, "");
        changeUserLanguage(createdUser.getId(), locale);
        createdUser.setLanguage(locale);

        ExpirationToken token = this.tokenDao.create(
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
    public Optional<User> getUserById(long id) {
        return userDao.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUserByToken(String token) {
        ExpirationToken expirationToken = tokenDao.getByToken(token).orElseThrow(() -> new UserNotFoundException("user.notfound"));
        return getUserById(expirationToken.getUser().getId());
    }

    @Transactional(readOnly = true)
    @Override
    public User changeUserPassword(String email, String password) {
        LOGGER.info("Changing password: {}", email);
        User user = getUserByEmail(email).orElseThrow(() -> new UserNotFoundException("notfound.user"));
        SaveUserBuilder saveUserBuilder = new SaveUserBuilder().withPassword(passwordEncoder.encode(password));
        return userDao.update(user.getId(), saveUserBuilder.build());
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getFollowers(long id) {
        return userDao.getFollowers(id).orElseThrow(() -> new UserNotFoundException("notfound.user"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getFollowing(long id) {
        return userDao.getFollowing(id).orElseThrow(() -> new UserNotFoundException("notfound.user"));
    }

    @Transactional(readOnly = true)
    @Override
    public FollowerFollowingCount getFollowerFollowingCount(long id) {
        return userDao.getFollowerFollowingCount(id).orElseThrow(() -> new UserNotFoundException("notfound.user"));
    }

    @Transactional(readOnly = true)
    @Override
    public Set<RoleType> getUserRoles(long id) {
        return userDao.findById(id).orElseThrow(() -> new UserNotFoundException("notfound.user")).getRoles();
    }

    @Transactional
    @Override
    public User followUserById(long userId, long otherId) {
        User toReturn = userDao.createFollow(userId, otherId).orElseThrow(() -> new UserNotFoundException("notfound.user"));
        LOGGER.info("User {} followed user {}", userId, otherId);
        return toReturn;
    }

    @Transactional
    @Override
    public User unfollowUserById(long userId, long otherId) {
        User toReturn = userDao.deleteFollow(userId, otherId).orElseThrow(() -> new UserNotFoundException("notfound.user"));
        LOGGER.info("User {} possibly unfollowed user {}", userId, otherId);
        return toReturn;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean userFollowsId(long userId, long otherId) {
        if (!userDao.exists(userId)) {
            throw new UserNotFoundException("notfound.currentuser");
        }
        if (!userDao.exists(otherId)) {
            throw new UserNotFoundException("notfound.otheruser");
        }
        return userDao.follows(userId, otherId);
    }

    @Transactional
    @Override
    public Optional<User> validateToken(String token) throws TokenExpiredException {
        Optional<ExpirationToken> expToken = tokenDao.getByToken(token);
        if (expToken.isPresent() && expToken.get().getExpiration().isBefore(LocalDateTime.now())) {
            LOGGER.error("Token for User {} expired", expToken.get().getUser().getId());
            throw new TokenExpiredException("token.expired");
        }
        if (!expToken.isPresent()) {
            return Optional.empty();
        }
        ExpirationToken expirationToken = expToken.get();
        User user = userDao.findById(expirationToken.getUser().getId()).orElseThrow(() -> new UserNotFoundException("illegal.state"));
        SaveUserBuilder userBuilder = new SaveUserBuilder().withEnabled(true).withPassword(expirationToken.getPassword());
        userDao.update(user.getId(), userBuilder.build());
        tokenDao.delete(expirationToken.getToken());
        LOGGER.info("User {} validated token", user.getId());
        return Optional.of(user);
    }

    @Transactional
    @Override
    public ExpirationToken resendToken(String email) throws UserAlreadyEnabled {
        User user = getUserByEmail(email).orElseThrow(() -> new UserNotFoundException("user.notfound"));
        if (user.isEnabled()) {
            LOGGER.error("User {} already enabled", user.getId());
            throw new UserAlreadyEnabled();
        }
        ExpirationToken token = tokenDao.findLastPasswordToken(user.getId()).orElseThrow(() -> new UserNotFoundException("user.notfound"));
        ExpirationToken newToken = tokenDao.create(user.getId(), token.getPassword(), LocalDateTime.now().plusHours(EXPIRATION_TIME));
        tokenDao.delete(token.getToken());
        mailingService.sendValidationTokenEmail(newToken, user);
        LOGGER.info("User {} resent token", user.getId());
        return newToken;
    }

    @Transactional
    @Override
    public ExpirationToken refreshToken(String token) {
        ExpirationToken expirationToken = tokenDao.getByToken(token).orElseThrow(() -> new UserNotFoundException("user.notfound"));
        User user = getUserById(expirationToken.getUser().getId()).orElseThrow(() -> new UserNotFoundException("user.notfound"));
        ExpirationToken newToken = tokenDao.create(user.getId(), expirationToken.getPassword(), LocalDateTime.now().plusHours(EXPIRATION_TIME));
        if (newToken == null) {
            LOGGER.error("User {} not found when refreshing token", user.getId());
            throw new UserNotFoundException("user.not.found");
        }
        tokenDao.delete(expirationToken.getToken());
        mailingService.sendValidationTokenEmail(newToken, user);
        return newToken;
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<User> searchUsers(Page page, String search, Ordering<UserOrderCriteria> ordering) {
        UserFilterBuilder userFilterBuilder = new UserFilterBuilder();
        if(search != null) {
            userFilterBuilder = userFilterBuilder.withSearch(search);
        }
        return userDao.findAll(page, userFilterBuilder.build(), ordering);
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<User> getUsersWhoReviewedSameGames(Page page, User currentUser, Ordering<UserOrderCriteria> ordering) {
        Set<Game> reviewedGames = gameService.getGamesReviewedByUser(currentUser.getId());
        return userDao.findAll(page,
                new UserFilterBuilder()
                        .withGamesPlayed(reviewedGames.stream().map(Game::getId).collect(Collectors.toList()))
                        .notWithId(currentUser.getId())
                        .build(),
                ordering);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean hasUserReviewedAnything(User currentUser) {
        return !gameService.getGamesReviewedByUser(currentUser.getId()).isEmpty();
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<User> getUsersWithSamePreferences(Page page, User currentUser, Ordering<UserOrderCriteria> ordering) {
        return userDao.findAll(page,
                new UserFilterBuilder()
                        .withPreferences(currentUser.getPreferences().stream().map(Genre::getId).collect(Collectors.toList()))
                        .notWithId(currentUser.getId())
                        .build(),
                ordering);
    }

    @Transactional
    @Override
    public ExpirationToken sendPasswordResetToken(String email) throws UserNotFoundException {
        User user = getUserByEmail(email).orElseThrow(() -> new UserNotFoundException("user.notfound"));
        ExpirationToken newToken = tokenDao.create(user.getId(), "", LocalDateTime.now().plusHours(EXPIRATION_TIME));
        mailingService.sendChangePasswordEmail(newToken, user);
        LOGGER.info("Sending User {} a password reset token", user.getId());
        return newToken;
    }

    @Transactional
    @Override
    public User resetPassword(String token, String password) throws TokenExpiredException, TokenNotFoundException {
        ExpirationToken existentToken = tokenDao.getByToken(token).orElseThrow(() -> new TokenNotFoundException("token.notfound"));
        if (existentToken.getExpiration().isBefore(LocalDateTime.now())) {
            LOGGER.error("Token for User {} password reset expired", existentToken.getUser().getId());
            throw new TokenExpiredException("token.expired");
        }
        User user = userDao.update(existentToken.getUser().getId(), new SaveUserBuilder().withPassword(passwordEncoder.encode(password)).withEnabled(true).build());
        LOGGER.info("User {} reset password", existentToken.getUser().getId());
        return user;
    }

    @Transactional(readOnly = true)
    @Override
    public Map<NotificationType, Boolean> getUserNotificationSettings(long userId) {
        Map<NotificationType, Boolean> notificationSettings = new HashMap<>();
        Set<NotificationType> disabledNotifications = getUserById(userId).orElseThrow(UserNotFoundException::new).getDisabledNotifications();
        for (NotificationType disabledNotification : disabledNotifications) {
            notificationSettings.put(disabledNotification, false);
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
    public Boolean isNotificationEnabled(long userId, NotificationType notificationType) {
        Set<NotificationType> disabledNotifications = getUserById(userId).orElseThrow(UserNotFoundException::new).getDisabledNotifications();
        return disabledNotifications.stream().noneMatch(disabledNotification -> disabledNotification.equals(notificationType));
    }

    @Transactional
    @Override
    public User setUserNotificationSettings(long userId, Map<NotificationType, Boolean> notificationSettings) {
        Map<NotificationType, Boolean> currentNotificationSettings = getUserNotificationSettings(userId);
        User modifiedUser = getUserById(userId).orElseThrow(UserNotFoundException::new);
        for (Map.Entry<NotificationType, Boolean> entry : notificationSettings.entrySet()) {
            if (entry.getValue() && !currentNotificationSettings.get(entry.getKey())) {
                modifiedUser = userDao.enableNotification(userId, entry.getKey().getTypeName());
                LOGGER.info("User {} enabled notification {}", userId, entry.getKey().getTypeName());
            } else if (!entry.getValue() && currentNotificationSettings.get(entry.getKey())) {
                modifiedUser = userDao.disableNotification(userId, entry.getKey().getTypeName());
                LOGGER.info("User {} disabled notification {}", userId, entry.getKey().getTypeName());
            }
        }
        return modifiedUser;
    }

    @Transactional
    @Override
    public User setPreferences(Set<Integer> genres, long userId){
        if (!userDao.exists(userId)) {
            throw new UserNotFoundException("notfound.user");
        }
        LOGGER.info("User {} set genre preferences", userId);
        User modifiedUser = userDao.setPreferences(genres, userId);
        if (!modifiedUser.getPreferences().isEmpty()) {
            missionService.addMissionProgress(modifiedUser.getId(), Mission.SETUP_PREFERENCES, 1f);
        }
        return modifiedUser;
    }

    @Transactional
    @Override
    public User modifyUserReputation(long id, int reputation) {
        User user = userDao.findById(id).orElseThrow(() -> new UserNotFoundException("user.notfound"));
        SaveUserBuilder builder = new SaveUserBuilder().withReputation(user.getReputation() + reputation);
        return userDao.update(id, builder.build());
    }

    @Transactional
    @Override
    public User changeUserAvatar(long userId, long imageId) throws InvalidAvatarException {
        if(imageId>AVATAR_AMOUNT || imageId<1)
            throw new InvalidAvatarException(imageId);
        User user = userDao.findById(userId).orElseThrow(UserNotFoundException::new);
        SaveUserBuilder builder = new SaveUserBuilder().withAvatar(imageId);
        missionService.addMissionProgress(user.getId(), Mission.CHANGE_AVATAR, 1f);
        LOGGER.info("User {} changed avatar to {}", userId, imageId);
        return userDao.update(userId, builder.build());
    }

    @Transactional
    @Override
    public User changeUserLanguage(long userId, Locale language) {
        User user = userDao.findById(userId).orElseThrow(UserNotFoundException::new);
        SaveUserBuilder builder = new SaveUserBuilder().withLanguage(language);
        LOGGER.info("User {} changed language to {}", userId, language);
        return userDao.update(user.getId(), builder.build());
    }
}
