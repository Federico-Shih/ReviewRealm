package ar.edu.itba.paw.persistence.tests.utils;

import ar.edu.itba.paw.models.ExpirationToken;
import ar.edu.itba.paw.models.User;

import java.time.LocalDateTime;

public class TokenTestModels {
    private final static String TOKEN1 = "aaaaaaaa";
    private final static User USER1 = UserTestModels.getUser2();
    private final static String PASSWORD = "";
    private final static LocalDateTime EXPIRATION = LocalDateTime.of(2018, 1, 1, 0, 0);

    private final static String TOKEN2 = "bbbbbbbb";
    private final static User USER2 = UserTestModels.getUser3();
    private final static String PASSWORD2 = "password-candidate";
    private final static LocalDateTime EXPIRATION2 = LocalDateTime.of(2018, 1, 1, 0, 0);

    private final static String TOKEN3 = "cccccccc";
    private final static User USER3 = UserTestModels.getUser3();
    private final static String PASSWORD3 = "";
    private final static LocalDateTime EXPIRATION3 = LocalDateTime.of(2018, 1, 1, 0, 0);

    private final static User CREATE_USER = UserTestModels.getUser2();
    private final static String CREATE_PASSWORD = "password";
    private final static LocalDateTime CREATE_EXPIRATION = LocalDateTime.of(2018, 1, 1, 0, 0);

    public static ExpirationToken getToken1() {
        return new ExpirationToken(TOKEN1, USER1, PASSWORD, EXPIRATION);
    }

    public static ExpirationToken getToken2() {
        return new ExpirationToken(TOKEN2, USER2, PASSWORD2, EXPIRATION2);
    }

    public static ExpirationToken getToken3() {
        return new ExpirationToken(TOKEN3, USER3, PASSWORD3, EXPIRATION3);
    }

    public static ExpirationToken getCreateToken() {
        return new ExpirationToken(CREATE_USER, CREATE_PASSWORD, CREATE_EXPIRATION);
    }
}
