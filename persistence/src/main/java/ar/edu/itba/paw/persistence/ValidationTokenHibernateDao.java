package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.ExpirationToken;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.ValidationTokenDao;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class ValidationTokenHibernateDao implements ValidationTokenDao {
    private static final Integer ID_LENGTH = 16;
    @PersistenceContext
    private EntityManager em;

    @Override
    public ExpirationToken create(long userId, String password, LocalDateTime expiration) {
        User user = em.getReference(User.class, userId);
        if (user == null) return null;
        ExpirationToken token = new ExpirationToken(generateId(), user, password, expiration);
        em.persist(token);
        return token;
    }

    @Override
    public Optional<ExpirationToken> findLastPasswordToken(long userId) {
        TypedQuery<ExpirationToken> query = em.createQuery("from ExpirationToken where user.id = :userId and password <> '' order by expiration desc", ExpirationToken.class);
        query.setParameter("userId", userId);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public boolean delete(String id) {
        ExpirationToken token = em.find(ExpirationToken.class, id);
        if (token == null) return false;
        em.remove(token);
        return true;
    }

    @Override
    public Optional<ExpirationToken> getByToken(String token) {
        ExpirationToken expirationToken = em.find(ExpirationToken.class, token);
        return Optional.ofNullable(expirationToken);
    }

    private String generateId() {
        return RandomStringUtils.randomAlphanumeric(ID_LENGTH);
    }

}
