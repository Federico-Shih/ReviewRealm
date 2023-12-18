package ar.edu.itba.paw.models;

import ar.edu.itba.paw.converters.LocalDateTimeConverter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
public class ExpirationToken {

    @Id
    @Column(name = "token")
    private String token;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "userid", referencedColumnName = "id")
    private User user;

    @Column(name = "expiration", nullable = false)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime expiration;

    public ExpirationToken(String token, User user, LocalDateTime expiration) {
        this.token = token;
        this.user = user;
        this.expiration = expiration;
    }

    protected ExpirationToken() {
        // Just for Hibernate
    }

    public ExpirationToken(User createUser, LocalDateTime createExpiration) {
        // For testing
        this.user = createUser;
        this.expiration = createExpiration;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public User getUser() {
        return user;
    }

}
