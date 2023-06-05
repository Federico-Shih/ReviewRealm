package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.UserFilter;
import ar.edu.itba.paw.dtos.saving.SaveUserDTO;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.NotificationType;
import ar.edu.itba.paw.models.DisabledNotification;
import ar.edu.itba.paw.models.FollowerFollowingCount;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.PaginationDao;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;

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
    public int update(long id, SaveUserDTO saveUserDTO) {
        final User user = em.find(User.class, id);
        if (user == null) {
            return 0;
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
        return 1;
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

    @Override
    public Paginated<User> findAll(Page page, UserFilter userFilter) {
        int pages = getPageCount(userFilter, page.getPageSize());
        if (page.getPageNumber() > pages) {
            return new Paginated<>(page.getPageNumber(), page.getPageSize(), pages, Collections.emptyList());
        }

        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<User> root = criteriaQuery.from(User.class);

        criteriaQuery.select(root.get("id"));
        addUserFilterQuery(userFilter, criteriaQuery, criteriaBuilder, root);
        final TypedQuery<Long> query = em.createQuery(criteriaQuery);
        query.setFirstResult(Math.toIntExact(page.getOffset()));
        query.setMaxResults(page.getPageSize());
        final List<Long> list = query.getResultList();

        final TypedQuery<User> userQuery = em.createQuery("from User where id IN :ids", User.class);
        userQuery.setParameter("ids", list);

        return new Paginated<>(page.getPageNumber(), page.getPageSize(), pages, userQuery.getResultList());
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
            criteriaQuery.where(criteriaBuilder.like(root.get("username"), "%" + userFilter.getSearch() + "%"));
        }
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public List<User> getFollowers(long id) {
        User user = em.find(User.class, id);
        if (user == null) return new ArrayList<>();
        return new ArrayList<>(user.getFollowers());
    }

    @Override
    public List<User> getFollowing(long id) {
        User user = em.find(User.class, id);
        if (user == null) return new ArrayList<>();
        return new ArrayList<>(user.getFollowing());
    }

    @Override
    public FollowerFollowingCount getFollowerFollowingCount(long id) {
        User user = em.find(User.class, id);
        if (user == null) return new FollowerFollowingCount(0, 0);
        return new FollowerFollowingCount(user.getFollowers().size(), user.getFollowing().size());
    }

    @Override
    public boolean createFollow(long userId, long id) {
        User user = em.find(User.class, userId);
        User userToFollow = em.find(User.class, id);
        user.getFollowing().add(userToFollow);
        return true;
    }

    @Override
    public boolean deleteFollow(long userId, long id) {
        User user = em.find(User.class, userId);
        User followedUser = em.getReference(User.class, id);
        if (user == null || followedUser == null) return false;
        if (!user.getFollowing().contains(followedUser)) {
            return false;
        }
        user.getFollowing().remove(followedUser);
        return true;
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
    public void setPreferences(Set<Integer> genres, long userId) {
        User user = em.find(User.class, userId);
        if (user == null) return;
        user.getPreferences().clear();
        for (Integer genreId : genres) {
            Genre genre = Genre.valueFrom(genreId);
            if (genre != null) {
                user.getPreferences().add(genre);
            }
        }
    }

    @Override
    public void disableNotification(long userId, String notificationType) {
        User user = em.find(User.class, userId);
        if (user != null) {
            final TypedQuery<DisabledNotification> disabledNotificationTypedQuery = em.createQuery("from DisabledNotification as dn", DisabledNotification.class);
            Optional<DisabledNotification> notification = disabledNotificationTypedQuery.getResultList().stream()
                    .filter(n -> n.getNotificationType().equals(NotificationType.valueFrom(notificationType)))
                    .findFirst();
            notification.ifPresent(disabledNotification -> user.getDisabledNotifications().add(disabledNotification));
        }
    }

    @Override
    public void enableNotification(long userId, String notificationType) {
        User user = em.find(User.class, userId);
        if (user != null) {
            final TypedQuery<DisabledNotification> disabledNotificationTypedQuery = em.createQuery("from DisabledNotification as dn", DisabledNotification.class);
            List<DisabledNotification> notifications = disabledNotificationTypedQuery.getResultList();
            Optional<DisabledNotification> notification = notifications.stream()
                    .filter(n -> n.getNotificationType().equals(NotificationType.valueFrom(notificationType)))
                    .findFirst();
            notification.ifPresent(disabledNotification -> user.getDisabledNotifications().remove(disabledNotification));
        }
    }
}
