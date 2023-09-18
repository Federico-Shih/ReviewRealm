package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.UserFilter;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.UserOrderCriteria;
import ar.edu.itba.paw.dtos.saving.SaveUserDTO;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.FollowerFollowingCount;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserDao {

    boolean exists(final long id);

    Optional<User> update(final long id, SaveUserDTO saveUserDTO);

    User create(String username, String email, String password);

    Optional<User> getByEmail(String email);

    Optional<User> getByUsername(String username);

    Paginated<User> findAll(Page page, UserFilter userFilter, Ordering<UserOrderCriteria> ordering);

    Optional<User> findById(final long id);

    Optional<Paginated<User>> getFollowers(long id, Page page);

    Optional<Paginated<User>> getFollowing(final long id, Page page);

    Optional<FollowerFollowingCount> getFollowerFollowingCount(final long id);

    Optional<User> createFollow(final long userId, final long id);

    Optional<User> deleteFollow(final long userId, final long id);

    boolean follows(final long userId, final long id);

    Set<Genre> getPreferences(final long userId);

    Optional<User> setPreferences(Set<Integer> genres, long userId);

    Long getTotalAmountOfUsers();

    Optional<User> disableNotification(long userId, String notificationType);

    Optional<User> enableNotification(long userId, String notificationType);

    boolean deleteFavoriteGameForUser(long userId, long gameId);
    Optional<User> replaceAllFavoriteGames(long userId, List<Long> gameIds);
}
