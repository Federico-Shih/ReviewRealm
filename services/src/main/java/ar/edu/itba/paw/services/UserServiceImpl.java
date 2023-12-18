package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.GameFilter;
import ar.edu.itba.paw.dtos.filtering.GameFilterBuilder;
import ar.edu.itba.paw.dtos.filtering.UserFilter;
import ar.edu.itba.paw.dtos.filtering.UserFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.UserOrderCriteria;
import ar.edu.itba.paw.dtos.saving.SaveUserBuilder;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Mission;
import ar.edu.itba.paw.enums.NotificationType;
import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
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

    private final GameDao gameDao;

    private final GameService gameService;

    private final MissionService missionService;
    private static final int EXPIRATION_TIME = 24; // 24hs
    private static final int AVATAR_AMOUNT = 6;

    @Autowired
    public UserServiceImpl(UserDao userDao,
                           PasswordEncoder passwordEncoder,
                           ValidationTokenDao tokenDao,
                           MailingService mailingService,
                           GameDao gameDao, GameService gameService, MissionService missionService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.tokenDao = tokenDao;
        this.mailingService = mailingService;
        this.gameDao = gameDao;
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
        LOGGER.info("Creating user with username: {} and email: {}",username, email);
        createdUser = userDao.create(username, email, passwordEncoder.encode(password));
        changeUserLanguage(createdUser.getId(), locale);
        createdUser.setLanguage(locale);

        ExpirationToken token = this.tokenDao.create(
                createdUser.getId(),
                LocalDateTime.now().plusHours(EXPIRATION_TIME));
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
    public Optional<User> getUserById(long id, Long currentUserId) {
        return userDao.findById(id, currentUserId);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUserByToken(String token) {
        ExpirationToken expirationToken = tokenDao.getByToken(token).orElseThrow(UserNotFoundException::new);
        return getUserById(expirationToken.getUser().getId());
    }

    @Transactional(readOnly = true)
    @Override
    public User changeUserPassword(String email, String password) {
        LOGGER.info("Changing password: {}", email);
        User user = getUserByEmail(email).orElseThrow(UserNotFoundException::new);
        SaveUserBuilder saveUserBuilder = new SaveUserBuilder().withPassword(passwordEncoder.encode(password));
        return userDao.update(user.getId(), saveUserBuilder.build()).orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<User> getFollowers(long id, Page page) {
        return userDao.getFollowers(id, page).orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<User> getFollowing(long id, Page page) {
        return userDao.getFollowing(id, page).orElseThrow(UserNotFoundException::new);
    }

    // TODO: remove
    @Transactional(readOnly = true)
    @Override
    public FollowerFollowingCount getFollowerFollowingCount(long id) {
        return userDao.getFollowerFollowingCount(id).orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<RoleType> getUserRoles(long id) {
        return userDao.findById(id).orElseThrow(UserNotFoundException::new).getRoles();
    }

    @Transactional
    @Override
    public User followUserById(long userId, long otherId) {
        if (userId == otherId) throw new UserIsSameException();
        if (userDao.follows(userId, otherId)) throw new UserAlreadyFollowing();
        User toReturn = userDao.createFollow(userId, otherId).orElseThrow(UserNotFoundException::new);
        LOGGER.info("User {} followed user {}", userId, otherId);
        missionService.addMissionProgress(userId, Mission.FOLLOWING_GOAL, 1);
        missionService.addMissionProgress(otherId, Mission.FOLLOWERS_GOAL, 1);
        return toReturn;
    }

    @Transactional
    @Override
    public User unfollowUserById(long userId, long otherId) {
        if (!userDao.follows(userId, otherId)) return null;
        Optional<User> toReturn = userDao.deleteFollow(userId, otherId);
        LOGGER.info("User {} unfollowed user {}", userId, otherId);
        if (toReturn.isPresent()) {
            missionService.addMissionProgress(userId, Mission.FOLLOWING_GOAL, -1);
            missionService.addMissionProgress(otherId, Mission.FOLLOWERS_GOAL, -1);
        }
        return toReturn.orElse(null);
    }

    @Transactional()
    @Override
    public User patchUser(long id, String password, Boolean enabled) {
        User user = userDao.findById(id).orElseThrow(UserNotFoundException::new);

        SaveUserBuilder builder = new SaveUserBuilder();
        if (password != null) {
            builder.withPassword(passwordEncoder.encode(password));
        }
        if (enabled != null) {
            if (user.isEnabled()) throw new UserAlreadyEnabled();
            builder.withEnabled(enabled);
        }
        User updated = userDao.update(
                        id, builder.build())
                .orElseThrow(UserNotFoundException::new);
        LOGGER.info("User {} patched", id);
        return updated;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean userFollowsId(long userId, long otherId) {
        if (!userDao.exists(userId)) {
            throw new UserNotFoundException();
        }
        if (!userDao.exists(otherId)) {
            throw new UserNotFoundException();
        }
        return userDao.follows(userId, otherId);
    }

    @Transactional
    @Override
    public Optional<User> validateToken(String token) throws TokenExpiredException {
        Optional<ExpirationToken> expToken = tokenDao.getByToken(token);
        if (expToken.isPresent() && expToken.get().getExpiration().isBefore(LocalDateTime.now())) {
            LOGGER.error("Token for User {} expired", expToken.get().getUser().getId());
            refreshToken(token);
            throw new TokenExpiredException();
        }
        if (!expToken.isPresent()) {
            return Optional.empty();
        }
        ExpirationToken expirationToken = expToken.get();
        User user = userDao.findById(expirationToken.getUser().getId()).orElseThrow(UserNotFoundException::new);
        SaveUserBuilder userBuilder = new SaveUserBuilder().withEnabled(true);
        userDao.update(user.getId(), userBuilder.build());
        tokenDao.delete(expirationToken.getToken());
        LOGGER.info("User {} validated token", user.getId());
        return Optional.of(user);
    }

    @Transactional
    @Override
    public ExpirationToken resendToken(String email) throws UserAlreadyEnabled {
        User user = getUserByEmail(email).orElseThrow(UserNotFoundException::new);
        if (user.isEnabled()) {
            LOGGER.error("User {} already enabled", user.getId());
            throw new UserAlreadyEnabled();
        }
        Optional<ExpirationToken> token = tokenDao.findLastPasswordToken(user.getId());
        token.ifPresent(expirationToken -> tokenDao.delete(expirationToken.getToken()));

        ExpirationToken newToken = tokenDao.create(user.getId(), LocalDateTime.now().plusHours(EXPIRATION_TIME));
        mailingService.sendValidationTokenEmail(newToken, user);
        LOGGER.info("User {} resent token", user.getId());
        return newToken;
    }

    @Transactional
    @Override
    public boolean deleteToken(String token) {
        return tokenDao.delete(token);
    }

    @Transactional
    @Override
    public ExpirationToken refreshToken(String token) {
        ExpirationToken expirationToken = tokenDao.getByToken(token).orElseThrow(UserNotFoundException::new);
        User user = expirationToken.getUser();
        ExpirationToken newToken = tokenDao.create(user.getId(), LocalDateTime.now().plusHours(EXPIRATION_TIME));
        if (newToken == null) {
            LOGGER.error("User {} not found when refreshing token", user.getId());
            throw new UserNotFoundException();
        }
        tokenDao.delete(expirationToken.getToken());
        mailingService.sendValidationTokenEmail(newToken, user);
        return newToken;
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<User> searchUsers(Page page, String search, Ordering<UserOrderCriteria> ordering, Long currentUserId) {
        UserFilterBuilder userFilterBuilder = new UserFilterBuilder();
        if (search != null) {
            userFilterBuilder = userFilterBuilder.withSearch(search);
        }
        return userDao.findAll(page, userFilterBuilder.build(), ordering, currentUserId);
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<User> getOtherUsers(Page page, Long userId, Ordering<UserOrderCriteria> ordering, Long currentUserId) {
        UserFilterBuilder userFilterBuilder = new UserFilterBuilder();
        if (userId != null)
            userFilterBuilder = userFilterBuilder.notWithId(userId);
        return userDao.findAll(page, userFilterBuilder.build(), ordering, currentUserId);
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<User> getUsersWhoReviewedSameGames(Page page, long userId, Ordering<UserOrderCriteria> ordering, Long currentUserId) {
        Set<Game> reviewedGames = gameService.getGamesReviewedByUser(userId);
        return userDao.findAll(page,
                new UserFilterBuilder()
                        .withGamesPlayed(reviewedGames.stream().map(Game::getId).collect(Collectors.toList()))
                        .notWithId(userId)
                        .build(),
                ordering,
                currentUserId);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean hasUserReviewedAnything(long userId) {
        return !gameService.getGamesReviewedByUser(userId).isEmpty();
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<User> getUsersWithSamePreferences(Page page, long userId, Ordering<UserOrderCriteria> ordering, Long currentUserId) {
        User user = getUserById(userId).orElseThrow(UserNotFoundException::new);
        return userDao.findAll(page,
                new UserFilterBuilder()
                        .withPreferences(user.getPreferences().stream().map(Genre::getId).collect(Collectors.toList()))
                        .notWithId(userId)
                        .build(),
                ordering,
                currentUserId);
    }

    @Transactional
    @Override
    public ExpirationToken sendPasswordResetToken(String email) throws UserNotFoundException {
        User user = getUserByEmail(email).orElseThrow(() -> new UserNotFoundException("user.notfound"));
        ExpirationToken newToken = tokenDao.create(user.getId(), LocalDateTime.now().plusHours(EXPIRATION_TIME));
        mailingService.sendChangePasswordEmail(newToken, user);
        LOGGER.info("Sending User {} a password reset token", user.getId());
        return newToken;
    }

    @Transactional
    @Override
    public User resetPassword(int id, String password) {
        User user = userDao.update(
                id,
                new SaveUserBuilder()
                        .withPassword(passwordEncoder.encode(password))
                        .withEnabled(true)
                        .build()
        ).orElseThrow(UserNotFoundException::new);
        LOGGER.info("User {} reset password", id);
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
                modifiedUser = userDao.enableNotification(userId, entry.getKey().getTypeName()).orElseThrow(UserNotFoundException::new);
                LOGGER.info("User {} enabled notification {}", userId, entry.getKey().getTypeName());
            } else if (!entry.getValue() && currentNotificationSettings.get(entry.getKey())) {
                modifiedUser = userDao.disableNotification(userId, entry.getKey().getTypeName()).orElseThrow(UserNotFoundException::new);
                LOGGER.info("User {} disabled notification {}", userId, entry.getKey().getTypeName());
            }
        }
        return modifiedUser;
    }

    @Transactional
    @Override
    public User setPreferences(Set<Integer> genres, long userId){
        LOGGER.info("User {} set genre preferences", userId);
        User modifiedUser = userDao.setPreferences(genres, userId).orElseThrow(UserNotFoundException::new);
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
        return userDao.update(id, builder.build()).orElseThrow(UserNotFoundException::new);
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
        return userDao.update(userId, builder.build()).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    @Override
    public User changeUserLanguage(long userId, Locale language) {
        User user = userDao.findById(userId).orElseThrow(UserNotFoundException::new);
        SaveUserBuilder builder = new SaveUserBuilder().withLanguage(language);
        LOGGER.info("User {} changed language to {}", userId, language);
        return userDao.update(user.getId(), builder.build()).orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<Game> getFavoriteGamesFromUser(Page page,long userId) {
        if (!getUserById(userId).isPresent())
            throw new UserNotFoundException();
        GameFilter filter = new GameFilterBuilder().withFavoriteGamesOf(userId).build();
        return gameDao.findAll(page, filter, new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING), userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Game> getPossibleFavGamesFromUser(long userId) {
        if(!getUserById(userId).isPresent())
            throw new UserNotFoundException();
        return gameDao.getFavoriteGamesCandidates(userId, 8);
    }

    @Transactional
    @Override
    public boolean deleteFavoriteGame(long userId, long gameId) {
        LOGGER.info("Possibly deleting gameId: {} from favorite games, for user {}", gameId, userId);
        return userDao.deleteFavoriteGameForUser(userId, gameId);
    }

    @Transactional
    @Override
    public User setFavoriteGames(long userId, List<Long> gameIds) {
        return userDao.replaceAllFavoriteGames(userId, gameIds==null? Collections.emptyList(): gameIds).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    @Override
    public boolean addFavoriteGame(long userId, long gameid) {
        if (!getUserById(userId).isPresent()) throw new UserNotFoundException();
        return userDao.addFavoriteGame(userId, gameid);
    }

    @Transactional(readOnly = true)
    @Override
    public Paginated<User> getUsers(Page page, UserFilter filter, Ordering<UserOrderCriteria> ordering, Long currentUserId) {

        if (filter.hasFollowersQuery()) {
            if (filter.isProperFollowersQuery()) {
                return userDao.getFollowers(filter.getFollowersQueryId(), page).orElseThrow(UserNotFoundException::new);
            }
            throw new ExclusiveFilterException("error.user.filter.followers");
        }
        if (filter.hasFollowingQuery()) {
            if (filter.isProperFollowingQuery()) {
                return userDao.getFollowing(filter.getFollowingQueryId(), page).orElseThrow(UserNotFoundException::new);
            }
            throw new ExclusiveFilterException("error.user.filter.following");
        }
        if (filter.hasSameGamesPlayedQuery()) {
            if (filter.isProperSameGamesPlayedAs()) {
                return this.getUsersWhoReviewedSameGames(page, filter.getSameGamesPlayedQueryId(), ordering, currentUserId);
            }
            throw new ExclusiveFilterException("error.user.filter.gamesplayed");
        }
        if (filter.hasSamePreferencesQuery()) {
            if (filter.isProperSamePreferencesAs()) {
                return this.getUsersWithSamePreferences(page, filter.getSamePreferencesQueryId(), ordering, currentUserId);
            }
            throw new ExclusiveFilterException("error.user.filter.preferences");
        }
        return userDao.findAll(page, filter, ordering, currentUserId);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ExpirationToken> getExpirationToken(String token) {
        return tokenDao.getByToken(token);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Set<NotificationType>> getNotifications(long id) {
        return userDao.findById(id).map(User::getDisabledNotifications);
    }
}
