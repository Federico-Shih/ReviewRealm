package ar.edu.itba.paw.persistence.tests.utils;

import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.NotificationType;
import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class UserTestModels {
    private UserTestModels() {
        /* For static access */
    }
    private static final int USER_ID = 1;
    private static final String USER_USERNAME = "username";
    private static final String USER_PASSWORD = "password";
    private static final String USER_EMAIL = "email";

    private static final int USER_ID2 = 2;
    private static final String USER_USERNAME2 = "username2";
    private static final String USER_EMAIL2 = "email2";

    private static final int USER_ID3 = 3;

    private static final String USER_USERNAME3 = "aaaa";
    private static final String USER_EMAIL3 = "email3";

    private static final int USER_ID4 = 4;
    private static final String USER_USERNAME4 = "bbbb";
    private static final String USER_EMAIL4 = "email4";

    private static final int USER_ID5 = 5;
    private static final String USER_USERNAME5 = "ab";
    private static final String USER_EMAIL5 = "email5";
    private static final int USER_AVATAR = 1;
    private static final int USER_REPUTATION = 100;
    private static final String USER_LANGUAGE = "en";
    private static final int USER_XP = 100;

    public static User getUser1() {
        User u1 = new User((long)USER_ID, USER_USERNAME, USER_EMAIL, USER_PASSWORD);
        u1.setPreferences(new HashSet<>(Arrays.asList(Genre.ACTION, Genre.ADVENTURE, Genre.CASUAL)));
        return u1;
    }

    public static User getUser2() {
        User u2 = new User((long)USER_ID2, USER_USERNAME2, USER_EMAIL2, USER_PASSWORD);
        u2.setPreferences(new HashSet<>(Collections.singletonList(Genre.ADVENTURE)));
        return u2;
    }

    public static User getUser3() {
        return new User((long)USER_ID3, USER_USERNAME3, USER_EMAIL3, USER_PASSWORD);
    }

    public static User getUser4() {
        User u4 = new User((long)USER_ID4, USER_USERNAME4, USER_EMAIL4, USER_PASSWORD);
        u4.setRoles(Collections.singleton(RoleType.MODERATOR));
        return u4;
    }

    public static User getUser5() {
        return new User(USER_ID5, USER_USERNAME5, USER_EMAIL5, USER_PASSWORD,true, USER_REPUTATION, new HashSet<>(Collections.singletonList(NotificationType.USER_I_FOLLOW_WRITES_REVIEW)), USER_LANGUAGE, USER_XP, Arrays.asList(GameTestModels.getSuperGameA(), GameTestModels.getSuperGameB(), GameTestModels.getSubnautica()));
    }
}
