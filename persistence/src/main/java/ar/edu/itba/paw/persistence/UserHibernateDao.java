package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.UserFilter;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.UserOrderCriteria;
import ar.edu.itba.paw.dtos.saving.SaveUserDTO;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.NotificationType;
import ar.edu.itba.paw.models.FollowerFollowingCount;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.helpers.DaoUtils;
import ar.edu.itba.paw.persistence.helpers.QueryBuilder;
import ar.edu.itba.paw.persistenceinterfaces.PaginationDao;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserHibernateDao implements UserDao, PaginationDao<UserFilter> {
    @PersistenceContext
    private EntityManager em;

    @Override
    public User create(String username, String email, String password) {
        final User user = new User(username, email, password);
        em.persist(user);
        return user;
    }

    @Override
    public Optional<User> update(long id, SaveUserDTO saveUserDTO) {
        final User user = em.find(User.class, id);
        if (user == null) {
            return Optional.empty();
        }
        if (saveUserDTO.isEnabled() != null) {
            user.setEnabled(saveUserDTO.isEnabled());
        }
        if (saveUserDTO.getAvatar() != null) {
            user.setAvatarId(saveUserDTO.getAvatar());
        }
        if (saveUserDTO.getReputation() != null) {
            user.setReputation(saveUserDTO.getReputation());
        }
        if (saveUserDTO.getReputation() != null) {
            user.setReputation(saveUserDTO.getReputation());
        }
        if (saveUserDTO.getEmail() != null) {
            user.setEmail(saveUserDTO.getEmail());
        }
        if (saveUserDTO.getUsername() != null) {
            user.setUsername(saveUserDTO.getUsername());
        }
        if (saveUserDTO.getPassword() != null) {
            user.setPassword(saveUserDTO.getPassword());
        }
        if (saveUserDTO.getLanguage() != null) {
            user.setLanguage(saveUserDTO.getLanguage());
        }
        if (saveUserDTO.getXp() != null) {
            user.setXp(saveUserDTO.getXp());
        }
        em.persist(user);
        return Optional.of(user);
    }

    @Override
    public boolean exists(long id) {
        return em.find(User.class, id) != null;
    }

    @Override
    public Optional<User> getByEmail(String email) {
        final TypedQuery<User> query = em.createQuery("from User as u where u.email = :email", User.class);
        query.setParameter("email", email);
        final List<User> list = query.getResultList();
        return list.stream().findFirst();
    }

    @Override
    public Optional<User> getByUsername(String username) {
        final TypedQuery<User> query = em.createQuery("from User as u where u.username = :username", User.class);
        query.setParameter("username", username);
        final List<User> list = query.getResultList();
        return list.stream().findFirst();
    }

    @Override   //For testing
    public Long getTotalAmountOfUsers() {
        final TypedQuery<Long> query = em.createQuery("select count(*) from User", Long.class);
        return query.getSingleResult();
    }

    private QueryBuilder getQueryBuilderFromFilter(UserFilter filter) {
        QueryBuilder queryBuilder = new QueryBuilder()
                .withExact("u.username", filter.getUsername())
                .withExact("u.email", filter.getEmail())
                .withExact("u.reputation", filter.getReputation())
                .withExact("u.enabled", filter.isEnabled())
                .withExact("u.id", filter.getId())
                .NOT().withExact("u.id", filter.getNotId())
                .withSimilar("u.username", filter.getSearch())
                .withList("gu.genreid", filter.getPreferences())
                .withList("r.gameid", filter.getGamesPlayed())
                ;

        return queryBuilder;
    }

    @Override
    public Paginated<User> findAll(Page page, UserFilter userFilter, Ordering<UserOrderCriteria> ordering, Long currentUserId) {
        int pages = getPageCount(userFilter, page.getPageSize());
        if (page.getPageNumber() > pages || page.getPageNumber() <= 0) {
            return new Paginated<>(page.getPageNumber(), page.getPageSize(), pages, Collections.emptyList());
        }

        QueryBuilder queryBuilder = getQueryBuilderFromFilter(userFilter);
        Query nativeQuery = em.createNativeQuery(
                "SELECT mainuserid FROM " +
                        "(" +
                        "SELECT distinct u.id as mainuserid,u.xp,u.reputation, coalesce((SELECT count(distinct f.userid) FROM followers as f WHERE f.following=u.id GROUP BY f.following), 0) as follower_count FROM " + toTableString(userFilter) + queryBuilder.toQuery() + ") as users"
                        + DaoUtils.toOrderString(ordering, true,"mainuserid"));
        DaoUtils.setNativeParameters(queryBuilder, nativeQuery);
        nativeQuery.setMaxResults(page.getPageSize());
        nativeQuery.setFirstResult(page.getOffset().intValue());

        @SuppressWarnings("unchecked") final List<Long> idlist = (List<Long>) nativeQuery.getResultList().stream().map(n -> ((Number) n).longValue()).collect(Collectors.toList());

        if (idlist.isEmpty()) {
            return new Paginated<>(page.getPageNumber(), page.getPageSize(), pages, new ArrayList<>());
        }

        final TypedQuery<User> userQuery = em.createQuery("from User where id IN :ids" + DaoUtils.toOrderString(ordering, false,null), User.class);
        userQuery.setParameter("ids", idlist);
        List<User> users = userQuery.getResultList();

        // Agrega si el usuario lo sigue o no.
        if (currentUserId != null) {
            final TypedQuery<Long> isFollowing = em.createQuery("select u.id from User current join current.following u where current.id = :id and u.id in :idlist", Long.class);
            isFollowing.setParameter("id", currentUserId);
            isFollowing.setParameter("idlist", idlist);
            Set<Long> isFollowingList = new HashSet<>(isFollowing.getResultList());
            users.forEach((user) -> user.setFollowing(isFollowingList.contains(user.getId())));
        }

        return new Paginated<>(page.getPageNumber(), page.getPageSize(), pages, users);
    }

    @Override
    public Long count(UserFilter filter) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        addUserFilterQuery(filter, criteriaQuery, criteriaBuilder, root);
        final TypedQuery<Long> query = em.createQuery(criteriaQuery);
        return query.getSingleResult();
    }


    @Override
    public Optional<User> findById(long id, Long currentUserId) {
        User user = em.find(User.class, id);
        if (user == null) return Optional.empty();
        if (currentUserId != null) {
            user.setFollowing(user.getFollowers().stream().anyMatch((u) -> u.getId().equals(currentUserId)));
        }
        return Optional.of(user);
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }


    @Override
    public Optional<Paginated<User>> getFollowers(long id, Page page) {
        User user = em.find(User.class, id);
        if (user == null) return Optional.empty();

        int pages = (int) Math.ceil((float) (em.createQuery("select count(f) from User u join u.followers f where u.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult()) / page.getPageSize());
        if (pages < page.getPageNumber() || page.getPageNumber() <= 0) {
            return Optional.of(new Paginated<>(page.getPageNumber(), page.getPageSize(), pages, new ArrayList<>()));
        }
        Query nativeQuery = em.createNativeQuery("SELECT f.userid FROM followers as f WHERE f.following = :id")
                .setParameter("id", id)
                .setMaxResults(page.getPageSize())
                .setFirstResult(page.getOffset().intValue());
        @SuppressWarnings("unchecked") final List<Long> idlist = (List<Long>) nativeQuery.getResultList().stream().map(n -> ((Number) n).longValue()).collect(Collectors.toList());
        if (idlist.isEmpty()) {
            return Optional.of(new Paginated<>(page.getPageNumber(), page.getPageSize(), pages, new ArrayList<>()));
        }
        TypedQuery<User> userQuery = em.createQuery("from User where id IN :ids", User.class);
        userQuery.setParameter("ids", idlist);
        return Optional.of(new Paginated<>(page.getPageNumber(), page.getPageSize(), pages, userQuery.getResultList()));
    }

    @Override
    public Optional<Paginated<User>> getFollowing(long id, Page page) {
        User user = em.find(User.class, id);
        if (user == null) return Optional.empty();

        int pages = (int) Math.ceil((float)(em.createQuery("select count(f) from User u join u.following f where u.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult()) / page.getPageSize());
        if (pages < page.getPageNumber() || page.getPageNumber() <= 0) {
            return Optional.of(new Paginated<>(page.getPageNumber(), page.getPageSize(), pages, new ArrayList<>()));
        }
        Query nativeQuery = em.createNativeQuery("SELECT f.following FROM followers as f WHERE f.userid = :id")
                .setParameter("id", id)
                .setMaxResults(page.getPageSize())
                .setFirstResult(page.getOffset().intValue());
        @SuppressWarnings("unchecked") final List<Long> idlist = (List<Long>) nativeQuery.getResultList().stream().map(n -> ((Number) n).longValue()).collect(Collectors.toList());
        if (idlist.isEmpty()) {
            return Optional.of(new Paginated<>(page.getPageNumber(), page.getPageSize(), pages, new ArrayList<>()));
        }
        TypedQuery<User> userQuery = em.createQuery("from User where id IN :ids", User.class);
        userQuery.setParameter("ids", idlist);
        return Optional.of(new Paginated<>(page.getPageNumber(), page.getPageSize(), pages, userQuery.getResultList()));
    }

    @Override
    public Optional<FollowerFollowingCount> getFollowerFollowingCount(long id) {
        User user = em.find(User.class, id);
        if (user == null) return Optional.empty();
        return Optional.of(new FollowerFollowingCount(user.getFollowers().size(), user.getFollowing().size()));
    }

    @Override
    public Optional<User>  createFollow(long userId, long id) {
        User user = em.find(User.class, userId);
        User userToFollow = em.getReference(User.class, id);
        if(user == null || userToFollow == null) return Optional.empty();
        user.getFollowing().add(userToFollow);
        return Optional.of(user);
    }

    @Override
    public Optional<User> deleteFollow(long userId, long id) {
        User user = em.find(User.class, userId);
        User followedUser = em.getReference(User.class, id);
        if (user == null || followedUser == null) return Optional.empty();
        user.getFollowing().remove(followedUser);
        return Optional.of(user);
    }

    @Override
    public boolean follows(long userId, long id) {
        User user = em.find(User.class, userId);
        User followedUser = em.getReference(User.class, id);
        if (user == null || followedUser == null) return false;
        return user.getFollowing().contains(followedUser);
    }

    @Override
    public Set<Genre> getPreferences(long userId) {
        User user = em.find(User.class, userId);
        if (user == null) return new HashSet<>();
        return user.getPreferences();
    }

    @Override
    public Optional<User> setPreferences(Set<Integer> genres, long userId) {
        User user = em.find(User.class, userId);
        if (user == null) return Optional.empty();
        user.getPreferences().clear();
        Set<Genre> preferences = user.getPreferences();
        for (Integer genreId : genres) {
            Genre genre = Genre.valueFrom(genreId);
            if (genre != null) {
               preferences.add(genre);
            }
        }
        return Optional.of(user);
    }

    @Override
    public Optional<User> disableNotification(long userId, String notificationType) {
        User user = em.find(User.class, userId);
        if (user == null) return Optional.empty();
        user.getDisabledNotifications().add(NotificationType.valueFrom(notificationType));
        return Optional.of(user);
    }

    @Override
    public Optional<User> enableNotification(long userId, String notificationType) {
        User user = em.find(User.class, userId);
        if (user == null) return Optional.empty();
        user.getDisabledNotifications().remove(NotificationType.valueFrom(notificationType));
        return Optional.of(user);
    }

    @Override
    public boolean deleteFavoriteGameForUser(long userId, long gameId) {
        final User user = em.find(User.class, userId);
        if(user==null) return false;
        return user.getFavoriteGames().removeIf(game -> game.getId() == gameId);
    }

    @Override
    public Optional<User> replaceAllFavoriteGames(long userId, List<Long> gameIds) {
        User user = em.find(User.class, userId);
        if (user == null) return Optional.empty();
        List<Game> favGames = user.getFavoriteGames();
        favGames.clear();
        for (Long gameId : gameIds) {
            Game game = em.find(Game.class, gameId);
            if (game != null && !game.getDeleted())
                favGames.add(game);
        }
        return Optional.of(user);
    }

    @Override
    public boolean addFavoriteGame(long userId, long gameid) {
        User user = em.createQuery("select u from User u where u.id = :id", User.class)
                .setParameter("id", userId)
                .getSingleResult();
        if (user == null) return false;
        if (user.getFavoriteGames().stream().anyMatch((game) -> game.getId().equals(gameid))) return false;
        Game game = em.find(Game.class, gameid);
        if (game != null && !game.getDeleted())
            user.getFavoriteGames().add(game);
        return true;
    }

    private String toTableString(UserFilter filter) {
        StringBuilder str = new StringBuilder();
        str.append("users as u ");
        if (filter.getPreferences() != null && !filter.getPreferences().isEmpty()) {
            str.append("JOIN genreforusers as gu ON u.id = gu.userid ");
        }
        if (filter.getGamesPlayed() != null && !filter.getGamesPlayed().isEmpty()) {
            str.append("JOIN reviews as r ON u.id = r.authorid ");
        }
        return str.toString();
    }

    private <T> void addUserFilterQuery(UserFilter userFilter, CriteriaQuery<T> criteriaQuery, CriteriaBuilder criteriaBuilder, Root<User> root) {
        if (userFilter.getUsername() != null) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("username"), userFilter.getUsername()));
        }
        if (userFilter.getEmail() != null) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("email"), userFilter.getEmail()));
        }
        if (userFilter.isEnabled() != null) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("enabled"), userFilter.isEnabled()));
        }
        if (userFilter.getReputation() != null) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("reputation"), userFilter.getReputation()));
        }
        if (userFilter.getId() != null) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("id"), userFilter.getId()));
        }
        if (userFilter.getSearch() != null) {
            String escapedSearch = userFilter.getSearch().replace("%","\\%").replace("_", "\\_");
            criteriaQuery.where(criteriaBuilder.like(root.get("username"), "%" + escapedSearch + "%"));
        }
    }
}
