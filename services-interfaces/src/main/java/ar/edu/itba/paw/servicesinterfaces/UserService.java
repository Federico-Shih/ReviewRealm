package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.UserOrderCriteria;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.NotificationType;
import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.*;

import java.util.*;

public interface UserService {
    User createUser(String username, String email, String password, Locale currentLocale) throws EmailAlreadyExistsException, UsernameAlreadyExistsException;

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserById(long id);

    Optional<User> getUserByToken(String token);

    User changeUserPassword(String email, String password);

    User setPreferences(Set<Integer> genres, long userId);

    List<User> getFollowers(long id);

    List<User> getFollowing(long id);

    FollowerFollowingCount getFollowerFollowingCount(long id);

    Set<RoleType> getUserRoles(long id);

    User followUserById(long userId, long otherId);

    User unfollowUserById(long userId, long otherId);

    boolean userFollowsId(long userId, long otherId);

    Optional<User> validateToken(String token) throws TokenExpiredException;

    ExpirationToken resendToken(String email) throws UserAlreadyEnabled;

    ExpirationToken refreshToken(String token);

    Paginated<User> searchUsers(Page page, String search, Ordering<UserOrderCriteria> ordering);

    Paginated<User> getUsersWhoReviewedSameGames(Page page, User currentUser, Ordering<UserOrderCriteria> ordering);

    boolean hasUserReviewedAnything(User currentUser);

    Paginated<User> getUsersWithSamePreferences(Page page, User currentUser, Ordering<UserOrderCriteria> ordering);

    ExpirationToken sendPasswordResetToken(String email) throws UserNotFoundException;

    User resetPassword(String token, String password) throws TokenExpiredException, TokenNotFoundException;

    Map<NotificationType, Boolean> getUserNotificationSettings(long userId);

    Boolean isNotificationEnabled(long userId, NotificationType notificationType);

    User setUserNotificationSettings(long userId, Map<NotificationType, Boolean> notificationSettings);

    User modifyUserReputation(long id, int reputation);

    User changeUserAvatar(long userId, long imageId) throws InvalidAvatarException;

    User changeUserLanguage(long userId, Locale language);
}
