package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.models.ExpirationToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ValidationTokenDao {

    ExpirationToken create(long userId, LocalDateTime expiration);

    Optional<ExpirationToken> findLastToken(long userId);

    boolean delete(String id);

    Optional<ExpirationToken> getByToken(String token);
}
