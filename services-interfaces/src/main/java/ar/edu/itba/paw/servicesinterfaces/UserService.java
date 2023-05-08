package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.enums.Genre;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(String username, String email, String password) throws EmailAlreadyExistsException, UsernameAlreadyExistsException;
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserById(Long id);
    void changeUserPassword(String email, String password);

    List<Genre> getPreferences(long userId);
    void setPreferences(List<Integer> genres, long userId);

    List<User> getFollowers(Long id);
    List<User> getFollowing(Long id);
    FollowerFollowingCount getFollowerFollowingCount(Long id);

    Optional<Follow> followUserById(Long userId, Long otherId);

    boolean unfollowUserById(Long userId, Long otherId);

    boolean userFollowsId(Long userId, Long otherId);

    List<Role> getUserRoles(Long userId);

    boolean validateToken(String token) throws TokenExpiredException;

    void resendToken(String email) throws UserAlreadyEnabled;

    void refreshToken(String token);

    Paginated<User> getSearchedUsers(int page, int pageSize, String search);

    void sendPasswordResetToken(String email) throws UserNotFoundException;

    boolean resetPassword(String token, String password) throws TokenExpiredException, TokenNotFoundException;

    boolean modifyUserReputation(long id, int reputation);
}
