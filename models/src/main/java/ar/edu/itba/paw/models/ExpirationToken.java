package ar.edu.itba.paw.models;

import java.time.LocalDateTime;

public class ExpirationToken {
    private final long id;
    private final String token;
    private final long userId;
    private final String password;
    private final LocalDateTime expiration;

    public ExpirationToken(long id, String token, long userid, String password, LocalDateTime expiration) {
        this.id = id;
        this.token = token;
        this.userId = userid;
        this.password = password;
        this.expiration = expiration;
    }

    public long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public long getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }
}
