package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.enums.NotificationType;
import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.FollowerFollowingCount;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.User;

import java.util.*;

public interface UserService {
    User createUser(String username, String email, String password, Locale currentLocale) throws EmailAlreadyExistsException, UsernameAlreadyExistsException;

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserById(Long id);

    Optional<User> getUserByToken(String token);

    void changeUserPassword(String email, String password);

    void setPreferences(Set<Integer> genres, long userId);

    List<User> getFollowers(Long id);

    List<User> getFollowing(Long id);

    FollowerFollowingCount getFollowerFollowingCount(Long id);

    Set<RoleType> getUserRoles(Long id);

    boolean followUserById(Long userId, Long otherId);

    boolean unfollowUserById(Long userId, Long otherId);

    boolean userFollowsId(Long userId, Long otherId);

    Optional<User> validateToken(String token) throws TokenExpiredException;

    void resendToken(String email) throws UserAlreadyEnabled;

    void refreshToken(String token);

    Paginated<User> getSearchedUsers(int page, int pageSize, String search);

    void sendPasswordResetToken(String email) throws UserNotFoundException;

    boolean resetPassword(String token, String password) throws TokenExpiredException, TokenNotFoundException;

    Map<NotificationType, Boolean> getUserNotificationSettings(Long userId);

    Boolean isNotificationEnabled(Long userId, NotificationType notificationType);

    void setUserNotificationSettings(Long userId, Map<NotificationType, Boolean> notificationSettings);

    boolean modifyUserReputation(long id, int reputation);

    void changeUserAvatar(long userId, long imageId) throws InvalidAvatarException;

    void changeUserLanguage(long userId, Locale language);
}
