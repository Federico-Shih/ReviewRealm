package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.models.ExpirationToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ValidationTokenDao {

    ExpirationToken create(String token, long userId, String password, LocalDateTime expiration);

    ExpirationToken refresh(long userId, String token);

    Optional<ExpirationToken> getByToken(String token);
}
