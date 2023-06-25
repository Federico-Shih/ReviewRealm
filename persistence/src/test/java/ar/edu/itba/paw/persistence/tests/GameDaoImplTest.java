package ar.edu.itba.paw.persistence.tests;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.GameFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.FeedbackType;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.helpers.CommonRowMappers;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class GameDaoImplTest {

    @PersistenceContext
    private EntityManager em;
    //Review fields

    private static final String TITLE = "title";

    private static final String CONTENT = "review text";

    private static final Timestamp TIMESTAMP = new Timestamp(System.currentTimeMillis());

    private static final Difficulty DIFFICULTY = Difficulty.HARD;

    private static final Platform PLATFORM = Platform.PC;

    private static final Double GAME_LENGTH = 100.78;

    private static final Boolean REPLAYABILITY = true;

    private static final Boolean COMPLETED = false;

    private static final FeedbackType REVIEW_FEEDBACK = FeedbackType.LIKE;

    //Game fields

    private static final String NAME =  "game name";

    private static final String DESCRIPTION = "game description";

    private static final String DEVELOPER = "game developer";

    private static final String PUBLISHER = "game publisher";

    private static final Boolean SUGGESTED = false;

    //User fields
    private static final String USERNAME1 = "username1";

    private static final String USERNAME2 = "username2";

    private static final String PASSWORD = "password";

    private static final String EMAIL1 = "email";

    private static final String EMAIL2 = "email2";

    private static final boolean ENABLED = true;

    //Genres

    private static final Genre GENRE1 = Genre.ACTION;

    private static final Genre GENRE2 = Genre.ADVENTURE;

    private static final Genre GENRE3 = Genre.CASUAL;

    //Images
    private static final String IMAGE_ID = "id1";

    private static final byte[] IMAGE = "E".getBytes();

    private static final String MEDIA_TYPE = "image/jpeg";


    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert insertForGames;
    private SimpleJdbcInsert insertForGenres;

    private SimpleJdbcInsert insertForFavGames;

    @Autowired
    private DataSource ds;

    @Autowired
    private GameDao gameDao;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        insertForGames = new SimpleJdbcInsert(ds).withTableName("games");
        insertForGenres = new SimpleJdbcInsert(ds).withTableName("genreforgames");
        insertForFavGames = new SimpleJdbcInsert(ds).withTableName("favoritegames");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "favoritegames");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "genreforgames");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "games");
    }

    @Before
    public void imageSetUp(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"images");
        SimpleJdbcInsert jdbcInsertForImages = new SimpleJdbcInsert(ds).withTableName("images");
        Map<String,Object> args = new HashMap<>();
        args.put("data",IMAGE);
        args.put("id",IMAGE_ID);
        args.put("mediaType",MEDIA_TYPE);
        jdbcInsertForImages.execute(args);
    }

    private Long userSetUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "genreforusers");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");

        SimpleJdbcInsert jdbcInsertForUsers = new SimpleJdbcInsert(ds).withTableName("users");
        SimpleJdbcInsert jdbcInsertForUsersGenres = new SimpleJdbcInsert(ds).withTableName("genreforusers");

        Map<String, Object> args = new HashMap<>();
        //Setup users
        args.put("username", USERNAME1);
        args.put("password", PASSWORD);
        args.put("email", EMAIL1);
        args.put("enabled", ENABLED);
        args.put("reputation", 0L);
        args.put("id", 1L);
        jdbcInsertForUsers.execute(args);
        args.put("username", USERNAME2);
        args.put("email", EMAIL2);
        args.put("id", 2L);
        jdbcInsertForUsers.execute(args);
        Long userId = 2L;

        //Setup user preferences
        args.clear();

        args.put("userid", userId);
        args.put("genreid", GENRE1.getId());
        jdbcInsertForUsersGenres.execute(args);
        args.put("genreid", GENRE2.getId());
        ;
        jdbcInsertForUsersGenres.execute(args);
        args.put("genreid", GENRE3.getId());
        jdbcInsertForUsersGenres.execute(args);
        return userId;
    }

    private Long reviewSetUp(Long reviewId, Long userId, Long gameId, Integer rating) {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "reviews");
        SimpleJdbcInsert jdbcInsertForReviews = new SimpleJdbcInsert(ds).withTableName("reviews");
        Map<String, Object> args = new HashMap<>();
        args.put("title", TITLE);
        args.put("content", CONTENT);
        args.put("createddate", TIMESTAMP);
        args.put("rating", rating);
        args.put("difficulty", DIFFICULTY);
        args.put("platform", PLATFORM);
        args.put("gamelength", GAME_LENGTH);
        args.put("replayability", REPLAYABILITY);
        args.put("completed", COMPLETED);
        args.put("feedback", REVIEW_FEEDBACK);
        args.put("authorid", userId);
        args.put("gameid", gameId);
        args.put("id", reviewId);
        args.put("dislikes", 0);
        args.put("likes", 0);
        jdbcInsertForReviews.execute(args);
        return reviewId;
    }

    private Long insertGame(Long id, Boolean suggested, Integer ratingSum, Integer reviewCount) {
        Map<String, Object> args = new HashMap<>();
        args.put("name", NAME);
        args.put("description", DESCRIPTION);
        args.put("developer", DEVELOPER);
        args.put("publisher", PUBLISHER);
        args.put("publishdate", TIMESTAMP);
        args.put("suggestion", suggested);
        args.put("image", IMAGE_ID);
        args.put("ratingsum", ratingSum);
        args.put("reviewcount", reviewCount);
        args.put("id", id);
        insertForGames.execute(args);
        return id;
    }

    @Rollback
    @Test
    public void testFindByIdNoGenres() {
        //Setup game
        Long gameId = insertGame(1L, SUGGESTED, 0, 0);

        //Test
        Optional<Game> game = gameDao.getById(gameId);

        //Assert
        Assert.assertTrue(game.isPresent());
        Assert.assertEquals(gameId,game.get().getId());
        Assert.assertTrue(game.get().getGenres().isEmpty());
    }

    @Rollback
    @Test
    public void testFindById() {
        //Setup game
        Long gameId = insertGame(1L, SUGGESTED, 0, 0);
        Map<String, Object> args = new HashMap<>();
        args.put("gameid", gameId);
        args.put("genreid", GENRE1.getId());
        insertForGenres.execute(args);
        args.put("genreid", GENRE2.getId());
        insertForGenres.execute(args);

        //Test
        Optional<Game> game = gameDao.getById(gameId);

        //Assert
        Assert.assertTrue(game.isPresent());
        Assert.assertEquals(gameId,game.get().getId());
        Assert.assertFalse(game.get().getGenres().isEmpty());
        List<Genre> genres = game.get().getGenres();
        Assert.assertTrue(genres.stream().anyMatch(g -> g.getId()==GENRE2.getId()));
    }

    @Rollback
    @Test
    public void testCreateWithGenres() {

        Game game = gameDao.create(NAME, DESCRIPTION, DEVELOPER, PUBLISHER, IMAGE_ID, Arrays.asList(GENRE1, GENRE2), LocalDate.now(), SUGGESTED);
        em.flush();
        List<Game> games = jdbcTemplate.query("SELECT * FROM games", CommonRowMappers.TEST_GAME_ROW_MAPPER);

        Assert.assertEquals(1, games.size());
        Assert.assertEquals(game.getId(), games.get(0).getId());

        List<Genre> genres = game.getGenres();

        Assert.assertEquals(2, genres.size());
        Assert.assertTrue(genres.stream().anyMatch(g -> g.getId() == GENRE1.getId()));
        Assert.assertTrue(genres.stream().anyMatch(g -> g.getId() == GENRE2.getId()));
    }

    @Rollback
    @Test
    public void testCreateNoGenres() {

        Game game = gameDao.create(NAME, DESCRIPTION, DEVELOPER, PUBLISHER, IMAGE_ID, new ArrayList<>(), LocalDate.now(), SUGGESTED);
        em.flush();
        List<Game> games = jdbcTemplate.query("SELECT * FROM games", CommonRowMappers.TEST_GAME_ROW_MAPPER);

        Assert.assertEquals(1, games.size());
        Assert.assertEquals(game.getId(), games.get(0).getId());
        List<Genre> genres = game.getGenres();

        Assert.assertEquals(0, genres.size());

    }

    @Rollback
    @Test
    public void testAddNewReview() {
        Long gameId = insertGame(1L, SUGGESTED, 10, 2);

        gameDao.addNewReview(gameId, 7);
        em.flush();
        List<Game> games = jdbcTemplate.query("Select * from games", CommonRowMappers.TEST_GAME_ROW_MAPPER);
        Assert.assertEquals(1, games.size());
        Game game = games.get(0);
        Assert.assertEquals(gameId, game.getId());
        Assert.assertEquals(17, (long) game.getRatingSum());
        Assert.assertEquals(3, (long) game.getReviewCount());

    }

    @Rollback
    @Test
    public void testModifyReview() {
        Long gameId = insertGame(1L, SUGGESTED, 10, 2);

        gameDao.modifyReview(gameId, 5, 10);
        em.flush();

        List<Game> games = jdbcTemplate.query("Select * from games", CommonRowMappers.TEST_GAME_ROW_MAPPER);
        Assert.assertEquals(1, games.size());
        Game game = games.get(0);
        Assert.assertEquals(gameId, game.getId());
        Assert.assertEquals(Double.valueOf((5 + 10) / 2.0), game.getAverageRating());

    }

    @Rollback
    @Test
    public void testDeleteReview() {
        Long gameId = insertGame(1L, SUGGESTED, 15, 3);

        gameDao.deleteReviewFromGame(gameId, 5);
        em.flush();
        List<Game> games = jdbcTemplate.query("Select * from games", CommonRowMappers.TEST_GAME_ROW_MAPPER);
        Assert.assertEquals(1, games.size());
        Game game = games.get(0);
        Assert.assertEquals(gameId, game.getId());
        Assert.assertEquals(Double.valueOf((10) / 2.0), game.getAverageRating());
    }

//    @Test
//    public void testSetSuggestedFalse(){
//        Long gameId = insertGame(1L, true, 15, 3);
//
//        boolean response = gameDao.setSuggestedFalse(gameId);
//        em.flush();
//
//        Assert.assertTrue(response);
//        Boolean suggested = jdbcTemplate.queryForObject("Select suggestion from games where games.id = ?",Boolean.class,gameId);
//        Assert.assertEquals(false,suggested);
//
//    }
//
//    @Test
//    public void testSetSuggestedFalseNotFound(){
//        Long gameId = insertGame(1L, true, 15, 3);
//
//        boolean response = gameDao.setSuggestedFalse(gameId+1);
//
//        Assert.assertFalse(response);
//    }

    @Test
    public void testDeleteGame(){
        Long gameId = insertGame(1L, true, 15, 3);

        boolean response = gameDao.deleteGame(gameId);
        em.flush();
        Assert.assertTrue(response);
        List<Game> games = jdbcTemplate.query("Select * from games", CommonRowMappers.TEST_GAME_ROW_MAPPER);
        Assert.assertEquals(0,games.size());
    }

    @Test
    public void testDeleteGameNotFound(){
        Long gameId = insertGame(1L, true, 15, 3);

        boolean response = gameDao.deleteGame(gameId+1);
        em.flush();
        Assert.assertFalse(response);
        List<Game> games = jdbcTemplate.query("Select * from games", CommonRowMappers.TEST_GAME_ROW_MAPPER);
        Assert.assertEquals(1,games.size());
    }

    @Test
    public void testReplaceAllFavoriteGames() {
        Long userId = userSetUp();
        Long gameId1 = insertGame(1L, SUGGESTED, 0, 0);
        Long gameId2 = insertGame(2L, SUGGESTED, 0, 0);
        Long gameId3 = insertGame(3L, SUGGESTED, 0, 0);

        Map<String, Object> args = new HashMap<>();
        args.put("userid", userId);
        args.put("gameid", gameId1);
        insertForFavGames.execute(args);
        args.put("gameid", gameId2);
        insertForFavGames.execute(args);

        List<Long> gameIds = Arrays.asList(gameId3);

        gameDao.replaceAllFavoriteGames(userId, gameIds);
        em.flush();

        List<Long> games = jdbcTemplate.queryForList("Select gameid from favoritegames where userid = ?", Long.class, userId);
        Assert.assertEquals(1, games.size());
        Assert.assertTrue(games.contains(gameId3));
        Assert.assertFalse(games.contains(gameId1));
        Assert.assertFalse(games.contains(gameId2));
    }

//    @Test
//    public void testGetFavoriteGamesFromUser() {
//        Long userId = userSetUp();
//        Long gameId1 = insertGame(1L, SUGGESTED, 0, 0);
//        Long gameId2 = insertGame(2L, SUGGESTED, 0, 0);
//
//        Map<String, Object> args = new HashMap<>();
//        args.put("userid", userId);
//        args.put("gameid", gameId1);
//        insertForFavGames.execute(args);
//
//        List<Game> games = gameDao.getFavoriteGamesFromUser(userId);
//
//        Assert.assertEquals(1, games.size());
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1)));
//        Assert.assertFalse(games.stream().anyMatch(g -> g.getId().equals(gameId2)));
//    }

    @Test
    public void testDeleteFavoriteGameForUser() {
        Long userId = userSetUp();
        Long gameId1 = insertGame(1L, SUGGESTED, 0, 0);
        Long gameId2 = insertGame(2L, SUGGESTED, 0, 0);
        Map<String, Object> args = new HashMap<>();
        args.put("userid", userId);
        args.put("gameid", gameId1);
        insertForFavGames.execute(args);

        args.put("gameid", gameId2);
        insertForFavGames.execute(args);


        gameDao.deleteFavoriteGameForUser(userId, gameId1);
        em.flush();

        List<Long> gameIds = jdbcTemplate.queryForList("Select gameid from favoritegames", Long.class);
        Assert.assertEquals(1, gameIds.size());
        Assert.assertTrue(gameIds.contains(gameId2));
    }

    @Test
    public void testGetFavoriteGamesCandidates() {
        Long userId = userSetUp();
        Long gameId1 = insertGame(1L, SUGGESTED, 0, 0);
        Long gameId2 = insertGame(2L, SUGGESTED, 0, 0);
        Long reviewId1 = reviewSetUp(1L, userId, gameId1, 8);

        List<Game> games = gameDao.getFavoriteGamesCandidates(userId, 7);

        Assert.assertEquals(1, games.size());
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1)));
        Assert.assertFalse(games.stream().anyMatch(g -> g.getId().equals(gameId2)));
    }

    @Test
    public void testGetFavoriteGamesCandidatesEmpty() {
        Long userId = userSetUp();
        Long gameId1 = insertGame(1L, SUGGESTED, 0, 0);
        Long gameId2 = insertGame(2L, SUGGESTED, 0, 0);
        Long reviewId1 = reviewSetUp(1L, userId, gameId1, 5);
        Long reviewId2 = reviewSetUp(2L, userId, gameId2, 3);

        List<Game> games = gameDao.getFavoriteGamesCandidates(userId, 7);

        Assert.assertTrue(games.isEmpty());
    }

    @Test
    public void testGetGenresByGame() {
        //Setup game
        Long gameId = insertGame(1L, SUGGESTED, 0, 0);
        Map<String, Object> args = new HashMap<>();
        args.put("gameid", gameId);
        args.put("genreid", GENRE1.getId());
        insertForGenres.execute(args);
        args.put("genreid", GENRE2.getId());
        insertForGenres.execute(args);

        List<Genre> genres = gameDao.getGenresByGame(gameId);

        Assert.assertEquals(2, genres.size());
        Assert.assertTrue(genres.stream().anyMatch(g -> g.getId() == GENRE1.getId()));
        Assert.assertTrue(genres.stream().anyMatch(g -> g.getId()==GENRE2.getId()));

    }

    @Test
    public void testFindAllNoFiltersAverageRatingOrdering() {
        Long gameId1 = insertGame(1L, SUGGESTED, 10, 1);
        Long gameId2 = insertGame(2L, SUGGESTED, 5, 2);
        GameFilterBuilder filterBuilder = new GameFilterBuilder();
        Ordering<GameOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING);
        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), ordering).getList();

        Assert.assertEquals(2, games.size());
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1)));
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId2)));
        Optional<Game> game = games.stream().findFirst();
        Assert.assertTrue(game.isPresent());
        Game game1 = game.get();
        Assert.assertEquals(gameId1, game1.getId());
    }

    @Test
    public void testFindAllGenreFilters() {
        Long gameId1 = insertGame(1L, SUGGESTED, 10, 1);
        Long gameId2 = insertGame(2L, SUGGESTED, 5, 2);
        Map<String, Object> args = new HashMap<>();
        args.put("gameid", gameId1);
        args.put("genreid", GENRE1.getId());
        insertForGenres.execute(args);
        args.put("genreid", GENRE2.getId());
        insertForGenres.execute(args);
        args.put("gameid", gameId2);
        insertForGenres.execute(args);
        args.put("genreid", GENRE3.getId());
        insertForGenres.execute(args);

        GameFilterBuilder filterBuilder = new GameFilterBuilder().withGameGenres(Arrays.asList(GENRE1.getId(), GENRE2.getId()));
        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();

        Assert.assertEquals(2,games.size());
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1)));
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId2)));
    }

    @Test
    public void testFindAllGenreFiltersEmpty() {
        Long gameId1 = insertGame(1L, SUGGESTED, 10, 1);
        Long gameId2 = insertGame(2L, SUGGESTED, 5, 2);
        Map<String, Object> args = new HashMap<>();
        args.put("gameid", gameId1);
        args.put("genreid", GENRE1.getId());
        insertForGenres.execute(args);
        args.put("genreid", GENRE2.getId());
        insertForGenres.execute(args);
        args.put("gameid", gameId2);
        insertForGenres.execute(args);

        GameFilterBuilder filterBuilder = new GameFilterBuilder().withGameGenres(Arrays.asList(GENRE3.getId()));
        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();

        Assert.assertTrue(games.isEmpty());
    }

    @Test
    public void testFindAllSearch() {
        Long gameId1 = insertGame(1L, SUGGESTED, 10, 1);
        Long gameId2 = insertGame(2L, SUGGESTED, 5, 2);
        Long gameId3 = insertGame(3L, SUGGESTED, 3, 3);
        GameFilterBuilder filterBuilder = new GameFilterBuilder().withGameContent(NAME);
        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();

        Assert.assertEquals(3, games.size());
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1)));
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId2)));
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId3)));
    }

    @Test
    public void testFindAllSearchEmpty() {
        Long gameId1 = insertGame(1L, SUGGESTED, 10, 1);
        Long gameId2 = insertGame(2L, SUGGESTED, 5, 2);
        Long gameId3 = insertGame(3L, SUGGESTED, 3, 3);
        GameFilterBuilder filterBuilder = new GameFilterBuilder().withGameContent("NOTHING HERE");
        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();

        Assert.assertTrue(games.isEmpty());
    }

    @Test
    public void testFindAllDeveloper() {
        Long gameId1 = insertGame(1L, SUGGESTED, 10, 1);
        Long gameId2 = insertGame(2L, SUGGESTED, 5, 2);

        GameFilterBuilder filterBuilder = new GameFilterBuilder().withDeveloper(DEVELOPER);
        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();

        Assert.assertEquals(2, games.size());
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1)));
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId2)));
    }

    @Test
    public void testFindAllPublishers() {
        Long gameId1 = insertGame(1L, SUGGESTED, 10, 1);
        Long gameId2 = insertGame(2L, SUGGESTED, 5, 2);

        GameFilterBuilder filterBuilder = new GameFilterBuilder().withPublisher(PUBLISHER);
        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();

        Assert.assertEquals(2, games.size());
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1)));
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId2)));
    }

    @Test
    public void testFindAllSearchSubString(){
        Long gameId1 = insertGame(1L, SUGGESTED, 10, 1);

        GameFilterBuilder filterBuilder = new GameFilterBuilder().withGameContent(NAME.substring(0,3));
        List<Game> games = gameDao.findAll(Page.with(1,10),filterBuilder.build(),new Ordering<>(OrderDirection.DESCENDING,GameOrderCriteria.AVERAGE_RATING)).getList();

        Assert.assertEquals(1,games.size());
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1)));
    }


    @Test
    public void testFindAllRatingRange() {
        Long gameId1 = insertGame(1L, SUGGESTED, 10, 1);
        Long gameId2 = insertGame(2L, SUGGESTED, 16, 2);
        Long gameId3 = insertGame(3L, SUGGESTED, 3, 3);
        GameFilterBuilder filterBuilder = new GameFilterBuilder().withRatingRange(8.0f, 10.0f, false);
        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();

        Assert.assertEquals(2, games.size());
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1)));
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId2)));
    }

    @Test
    public void testFindAllRatingRangeEmpty() {
        Long gameId1 = insertGame(1L, SUGGESTED, 17, 2);
        Long gameId2 = insertGame(2L, SUGGESTED, 16, 2);
        Long gameId3 = insertGame(3L, SUGGESTED, 3, 3);
        GameFilterBuilder filterBuilder = new GameFilterBuilder().withRatingRange(9.5f, 9.9f, false);
        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();

        Assert.assertTrue(games.isEmpty());
    }

    @Test
    public void testGetRecommendationsForUser() {
        //Setup game
        Long gameId1 = insertGame(1L, SUGGESTED, 0, 0);
        Long gameId2 = insertGame(2L, SUGGESTED, 0, 0);
        //Setup genres
        Map<String, Object> args = new HashMap<>();
        args.put("gameid", gameId1);
        args.put("genreid", GENRE1.getId());
        insertForGenres.execute(args);
        args.put("genreid", GENRE2.getId());
        insertForGenres.execute(args);
        args.put("gameid", gameId2);
        insertForGenres.execute(args);
        args.put("genreid", GENRE3.getId());
        insertForGenres.execute(args);
        //Setup user
        List<Integer> genres = Arrays.asList(GENRE2.getId(), GENRE3.getId());

        List<Game> games = gameDao.getRecommendationsForUser(genres,new ArrayList<>());

        Assert.assertEquals(2,games.size());
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1) ));
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId2) ));
    }

    @Test
    public void testGetRecommendationsForUserExclude() {
        //Setup game
        Long gameId1 = insertGame(1L, SUGGESTED, 0, 0);
        Long gameId2 = insertGame(2L, SUGGESTED, 0, 0);
        //Setup genres
        Map<String, Object> args = new HashMap<>();
        args.put("gameid", gameId1);
        args.put("genreid", GENRE1.getId());
        insertForGenres.execute(args);
        args.put("genreid", GENRE2.getId());
        insertForGenres.execute(args);
        args.put("gameid", gameId2);
        insertForGenres.execute(args);
        args.put("genreid", GENRE3.getId());
        insertForGenres.execute(args);
        //Setup user
        List<Integer> genres = Arrays.asList(GENRE2.getId(), GENRE3.getId());

        List<Game> games = gameDao.getRecommendationsForUser(genres,Arrays.asList(gameId1));

        Assert.assertEquals(1,games.size());
        Assert.assertFalse(games.stream().anyMatch(g -> g.getId().equals(gameId1) ));
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId2) ));
    }

//    @Test
//    public void testGetGamesReviewdByUser(){
//        Long gameId = insertGame(1L, SUGGESTED, 0, 0);
//        Long userId = userSetUp();
//        Long reviewid = reviewSetUp(1L, userId, gameId, 1);
//
//        Set<Game> games = gameDao.getGamesReviewedByUser(userId);
//
//        Assert.assertEquals(1,games.size());
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId)));
//
//    }
//
//    @Test
//    public void testGetGamesReviewdByUserEmpty(){
//        Long gameId = insertGame(1L, SUGGESTED, 0, 0);
//        Long userId = userSetUp();
//
//        Set<Game> games = gameDao.getGamesReviewedByUser(userId);
//
//        Assert.assertTrue(games.isEmpty());
//
//    }


}
