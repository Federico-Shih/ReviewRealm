package ar.edu.itba.paw.persistence.tests.utils;

import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Image;

import java.time.LocalDate;
import java.util.*;

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

    //        Game game = gameDao.create(NAME, DESCRIPTION, DEVELOPER, PUBLISHER, IMAGE_ID, Arrays.asList(GENRE1, GENRE2), LocalDate.now(), SUGGESTED);
    private static final String CREATE_GAME_NAME = "Temporal";
    private static final String CREATE_GAME_DESCRIPTION = "Description";
    private static final String CREATE_GAME_DEVELOPER = "Studio";

    private static final String CREATE_GAME_PUBLISHER = "Publisher";
    private static final String CREATE_GAME_IMAGE_ID = "id1";
    private static final List<Genre> CREATE_GAME_GENRES = new ArrayList<>(Arrays.asList(Genre.ACTION, Genre.ADVENTURE));
    private static final String CREATE_GAME_RELEASE_DATE = "2090-07-15";
    private static final boolean CREATE_GAME_SUGGESTED = false;

    private static final byte[] image = new byte[0];
    private static final List<Genre> COMMON_GENRES= new ArrayList<>(Collections.singletonList(Genre.ADVENTURE));
    // Models in Populator
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

    /*
        long id,
    String name,
    String description,
    String developer,
    String publisher,
    LocalDate publishDate,
    int ratingsum,
    int reviewcount
     */
    // Model for Create
    public static Game getCreateGame() {
        return new Game(CREATE_GAME_NAME,
                CREATE_GAME_DESCRIPTION,
                CREATE_GAME_DEVELOPER,
                CREATE_GAME_PUBLISHER,
                new Image(CREATE_GAME_IMAGE_ID, "jpeg", image),
                CREATE_GAME_GENRES,
                LocalDate.parse(CREATE_GAME_RELEASE_DATE),
                CREATE_GAME_SUGGESTED,
                UserTestModels.getUser1());
    }

    public static Game getCreateGameNoGenres() {
        return new Game(CREATE_GAME_NAME,
                CREATE_GAME_DESCRIPTION,
                CREATE_GAME_DEVELOPER,
                CREATE_GAME_PUBLISHER,
                new Image(CREATE_GAME_IMAGE_ID, "jpeg", image),
                new ArrayList<>(),
                LocalDate.parse(CREATE_GAME_RELEASE_DATE),
                CREATE_GAME_SUGGESTED,
                UserTestModels.getUser1());
    }

    public static Game getGameWithGenres() {
        return getSuperGameA();
    }
}
