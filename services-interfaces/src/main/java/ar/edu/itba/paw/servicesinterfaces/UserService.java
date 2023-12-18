package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.UserFilter;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.UserOrderCriteria;
import ar.edu.itba.paw.enums.NotificationType;
import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.*;

import java.util.*;

public interface UserService {

    User createUser(String username, String email, String password, Locale currentLocale) throws EmailAlreadyExistsException, UsernameAlreadyExistsException;

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserById(long id, Long currentUserId);

    Optional<User> getUserById(long id);

    Optional<User> getUserByToken(String token);

    User changeUserPassword(String email, String password);

    User setPreferences(Set<Integer> genres, long userId);

    Paginated<User> getFollowers(long id, Page page);
    
    Paginated<User> getFollowing(long id, Page page);

    Set<RoleType> getUserRoles(long id);
    
    User followUserById(long userId, long otherId);
    
    User unfollowUserById(long userId, long otherId);

    User patchUser(long id, String password, Boolean enabled);

    boolean userFollowsId(long userId, long otherId);
    
    Optional<User> validateToken(String token) throws TokenExpiredException;
    
    ExpirationToken resendToken(String email) throws UserAlreadyEnabled;

    boolean deleteToken(String token);

    ExpirationToken refreshToken(String token);

    Paginated<User> getUsersWhoReviewedSameGames(Page page, long userId, Ordering<UserOrderCriteria> ordering, Long currentUserId);

    Paginated<User> getUsersWithSamePreferences(Page page, long userId, Ordering<UserOrderCriteria> ordering, Long currentUserId);
    
    ExpirationToken sendPasswordResetToken(String email) throws UserNotFoundException;

    
    Map<NotificationType, Boolean> getUserNotificationSettings(long userId);
    
    Boolean isNotificationEnabled(long userId, NotificationType notificationType);
    
    User setUserNotificationSettings(long userId, Map<NotificationType, Boolean> notificationSettings);
    
    User modifyUserReputation(long id, int reputation);
    
    User changeUserAvatar(long userId, long imageId) throws InvalidAvatarException;
    
    User changeUserLanguage(long userId, Locale language);
    
    Paginated<Game> getFavoriteGamesFromUser(Page page,long userId);

    
    boolean deleteFavoriteGame(long userId, long gameId);


    boolean addFavoriteGame(long userId, long gameid);

    Paginated<User> getUsers(Page page, UserFilter filter, Ordering<UserOrderCriteria> ordering, Long currentUserId);

    Optional<ExpirationToken> getExpirationToken(String token);

}
