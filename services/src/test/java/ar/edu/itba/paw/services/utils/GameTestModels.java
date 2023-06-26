package ar.edu.itba.paw.services.utils;

import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Image;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameTestModels {
    private GameTestModels() {
        /* For static access */
    }
    private static final int SUPER_GAMEA_ID = 1;
    private static final String SUPER_GAMEA_NAME = "Super Game A";
    private static final String SUPER_GAMEA_DESCRIPTION = "A thrilling adventure";
    private static final String SUPER_GAMEA_STUDIO = "Game Studios";
    private static final String SUPER_GAMEA_PUBLISHER = "Game Publisher";
    private static final boolean SUPER_GAMEA_IS_SUGGESTION = false;
    private static final String IMAGEID = "id1";
    private static final String SUPER_GAMEA_RELEASE_DATE = "2090-07-15";
    private static final int SUPER_GAMEA_RATING = 0;
    private static final int SUPER_GAMEA_RATING_COUNT = 0;

    private static final int SUPER_GAMEB_ID = 2;
    private static final String SUPER_GAMEB_NAME = "Super Game B";
    private static final String SUPER_GAMEB_DESCRIPTION = "BBBBBBBBBBBBBB";

    private static final int SUBNAUTICA_ID = 3;
    private static final String SUBNAUTICA_NAME = "Subnautica";
    private static final String SUBNAUTICA_DESCRIPTION = "Juegos";
    private static final boolean SUBNAUTICA_IS_SUGGESTION = true;
    private static final String SUBNAUTICA_RELEASE_DATE = "2090-07-15";
    private static final int SUBNAUTICA_RATING = 3;
    private static final int SUBNAUTICA_RATING_COUNT = 3;

    private static final int SUBNAUTICA2_ID = 4;
    private static final String SUBNAUTICA2_NAME = "Subnautica 2";
    private static final String SUBNAUTICA2_DESCRIPTION = "Juegos";
    private static final boolean SUBNAUTICA2_IS_SUGGESTION = false;

    private static final int SUBNAUTICA2_RATING = 1;
    private static final int SUBNAUTICA2_RATING_COUNT = 1;

    private static final List<Genre> SUPER_GAMEA_GENRES = new ArrayList<>(Arrays.asList(Genre.ACTION, Genre.ADVENTURE));
    private static final List<Genre> SUPER_GAMEB_GENRES = new ArrayList<>(Arrays.asList(Genre.CASUAL, Genre.ADVENTURE));


    public static Game getSuperGameA() {
        return new Game(SUPER_GAMEA_ID, SUPER_GAMEA_NAME, SUPER_GAMEA_DESCRIPTION, SUPER_GAMEA_STUDIO, SUPER_GAMEA_PUBLISHER, SUPER_GAMEA_IS_SUGGESTION, IMAGEID, SUPER_GAMEA_RELEASE_DATE, SUPER_GAMEA_RATING, SUPER_GAMEA_RATING_COUNT, SUPER_GAMEA_GENRES);
    }

    public static Game getSuperGameB() {
        return new Game(SUPER_GAMEB_ID, SUPER_GAMEB_NAME, SUPER_GAMEB_DESCRIPTION, SUPER_GAMEA_STUDIO, SUPER_GAMEA_PUBLISHER, SUPER_GAMEA_IS_SUGGESTION, IMAGEID, SUPER_GAMEA_RELEASE_DATE, 10, 1, SUPER_GAMEB_GENRES);
    }

    public static Game getSubnautica() {
        return new Game(SUBNAUTICA_ID, SUBNAUTICA_NAME, SUBNAUTICA_DESCRIPTION, SUPER_GAMEA_STUDIO, SUPER_GAMEA_PUBLISHER, SUBNAUTICA_IS_SUGGESTION, IMAGEID, SUBNAUTICA_RELEASE_DATE, SUBNAUTICA_RATING, SUBNAUTICA_RATING_COUNT, new ArrayList<>());
    }

    public static Game getSubnautica2() {
        return new Game(SUBNAUTICA2_ID, SUBNAUTICA2_NAME, SUBNAUTICA2_DESCRIPTION, SUPER_GAMEA_STUDIO, SUPER_GAMEA_PUBLISHER, SUBNAUTICA2_IS_SUGGESTION, IMAGEID, SUBNAUTICA_RELEASE_DATE, SUBNAUTICA2_RATING, SUBNAUTICA2_RATING_COUNT, new ArrayList<>());
    }

    public static Game getGameWithGenres() {
        return getSuperGameA();
    }
}
