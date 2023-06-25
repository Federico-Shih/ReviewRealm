package ar.edu.itba.paw.persistence.tests;

import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.helpers.CommonRowMappers;
import ar.edu.itba.paw.persistence.tests.utils.GameTestModels;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class GameDaoImplTest {

    private static int RATING_VALUE = 7;
    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;


    @Autowired
    private DataSource ds;

    @Autowired
    private GameDao gameDao;

    private Game testGameNoGenres;
    private Game testGame;
    private Game createGame;

    private Game createGameNoGenres;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        testGameNoGenres = GameTestModels.getSubnautica();
        testGame = GameTestModels.getSuperGameA();
        createGame = GameTestModels.getCreateGame();
        createGameNoGenres = GameTestModels.getCreateGameNoGenres();
    }

    @Rollback
    @Test
    public void testFindByIdNoGenres() {
        //Test
        Optional<Game> game = gameDao.getById(testGameNoGenres.getId());

        //Assert
        Assert.assertTrue(game.isPresent());
        Assert.assertEquals(testGameNoGenres,game.get());
        Assert.assertTrue(game.get().getGenres().isEmpty());
    }

    @Rollback
    @Test
    public void testFindById() {
        //Test
        Optional<Game> game = gameDao.getById(testGame.getId());

        //Assert
        Assert.assertTrue(game.isPresent());
        Assert.assertEquals(testGame, game.get());
        Assert.assertFalse(game.get().getGenres().isEmpty());
        List<Genre> genres = game.get().getGenres();
        Assert.assertEquals(new HashSet<>(testGame.getGenres()), new HashSet<>(genres));
    }

    @Rollback
    @Test
    public void testCreateWithGenres() {
        Game game = gameDao.create(createGame.getName(),
                createGame.getDescription(),
                createGame.getDeveloper(),
                createGame.getPublisher(),
                createGame.getImage().getId(),
                createGame.getGenres(),
                createGame.getPublishDate(),
                createGame.getSuggestion());
        em.flush();
        Assert.assertNotNull(game);
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "games", String.format("name = '%s'", createGame.getName())));
        Game gameFromDB = jdbcTemplate.queryForObject("SELECT * FROM games WHERE name = ?", CommonRowMappers.TEST_GAME_ROW_MAPPER, game.getName());
        Assert.assertEquals(2, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "genreforgames", String.format("gameid = %d", gameFromDB.getId())));
    }

    @Rollback
    @Test
    public void testFindNoId() {
        //Test
        Optional<Game> game = gameDao.getById(5L);

        //Assert
        Assert.assertFalse(game.isPresent());
    }

    @Rollback
    @Test
    public void testCreateNoGenres() {
        Game game = gameDao.create(createGameNoGenres.getName(),
                createGameNoGenres.getDescription(),
                createGameNoGenres.getDeveloper(),
                createGameNoGenres.getPublisher(),
                createGameNoGenres.getImage().getId(),
                createGameNoGenres.getGenres(),
                createGameNoGenres.getPublishDate(),
                createGameNoGenres.getSuggestion());
        em.flush();
        Assert.assertNotNull(game);
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "games", String.format("name = '%s'", createGameNoGenres.getName())));
        Game gameFromDB = jdbcTemplate.queryForObject("SELECT * FROM games WHERE name = ?", CommonRowMappers.TEST_GAME_ROW_MAPPER, createGameNoGenres.getName());
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "genreforgames", String.format("gameid = %d", gameFromDB.getId())));
    }

    @Rollback
    @Test
    public void testAddNewReview() {
        gameDao.addNewReview(testGame.getId(), RATING_VALUE);
        em.flush();
        Object[] params = {testGame.getId()};
        Game dbGame = jdbcTemplate.queryForObject("Select * from games where id = ?", params, CommonRowMappers.TEST_GAME_ROW_MAPPER);
        Assert.assertNotNull(dbGame);
        Assert.assertEquals(testGame.getId(), dbGame.getId());
        Assert.assertEquals(testGame.getRatingSum() + RATING_VALUE, (long) dbGame.getRatingSum());
        Assert.assertEquals(testGame.getReviewCount() + 1, (long) dbGame.getReviewCount());
    }
//
//    @Rollback
//    @Test
//    public void testModifyReview() {
//        Long gameId = insertGame(1L, SUGGESTED, 10, 2);
//
//        gameDao.modifyReview(gameId, 5, 10);
//        em.flush();
//
//        List<Game> games = jdbcTemplate.query("Select * from games", CommonRowMappers.TEST_GAME_ROW_MAPPER);
//        Assert.assertEquals(1, games.size());
//        Game game = games.get(0);
//        Assert.assertEquals(gameId, game.getId());
//        Assert.assertEquals(Double.valueOf((5 + 10) / 2.0), game.getAverageRating());
//
//    }
//
//    @Rollback
//    @Test
//    public void testDeleteReview() {
//        Long gameId = insertGame(1L, SUGGESTED, 15, 3);
//
//        gameDao.deleteReviewFromGame(gameId, 5);
//        em.flush();
//        List<Game> games = jdbcTemplate.query("Select * from games", CommonRowMappers.TEST_GAME_ROW_MAPPER);
//        Assert.assertEquals(1, games.size());
//        Game game = games.get(0);
//        Assert.assertEquals(gameId, game.getId());
//        Assert.assertEquals(Double.valueOf((10) / 2.0), game.getAverageRating());
//    }
//
////    @Test
////    public void testSetSuggestedFalse(){
////        Long gameId = insertGame(1L, true, 15, 3);
////
////        boolean response = gameDao.setSuggestedFalse(gameId);
////        em.flush();
////
////        Assert.assertTrue(response);
////        Boolean suggested = jdbcTemplate.queryForObject("Select suggestion from games where games.id = ?",Boolean.class,gameId);
////        Assert.assertEquals(false,suggested);
////
////    }
////
////    @Test
////    public void testSetSuggestedFalseNotFound(){
////        Long gameId = insertGame(1L, true, 15, 3);
////
////        boolean response = gameDao.setSuggestedFalse(gameId+1);
////
////        Assert.assertFalse(response);
////    }
//
//    @Test
//    public void testDeleteGame(){
//        Long gameId = insertGame(1L, true, 15, 3);
//
//        boolean response = gameDao.deleteGame(gameId);
//        em.flush();
//        Assert.assertTrue(response);
//        List<Game> games = jdbcTemplate.query("Select * from games", CommonRowMappers.TEST_GAME_ROW_MAPPER);
//        Assert.assertEquals(0,games.size());
//    }
//
//    @Test
//    public void testDeleteGameNotFound(){
//        Long gameId = insertGame(1L, true, 15, 3);
//
//        boolean response = gameDao.deleteGame(gameId+1);
//        em.flush();
//        Assert.assertFalse(response);
//        List<Game> games = jdbcTemplate.query("Select * from games", CommonRowMappers.TEST_GAME_ROW_MAPPER);
//        Assert.assertEquals(1,games.size());
//    }
//
//    @Test
//    public void testReplaceAllFavoriteGames() {
//        Long userId = userSetUp();
//        Long gameId1 = insertGame(1L, SUGGESTED, 0, 0);
//        Long gameId2 = insertGame(2L, SUGGESTED, 0, 0);
//        Long gameId3 = insertGame(3L, SUGGESTED, 0, 0);
//
//        Map<String, Object> args = new HashMap<>();
//        args.put("userid", userId);
//        args.put("gameid", gameId1);
//        insertForFavGames.execute(args);
//        args.put("gameid", gameId2);
//        insertForFavGames.execute(args);
//
//        List<Long> gameIds = Arrays.asList(gameId3);
//
//        gameDao.replaceAllFavoriteGames(userId, gameIds);
//        em.flush();
//
//        List<Long> games = jdbcTemplate.queryForList("Select gameid from favoritegames where userid = ?", Long.class, userId);
//        Assert.assertEquals(1, games.size());
//        Assert.assertTrue(games.contains(gameId3));
//        Assert.assertFalse(games.contains(gameId1));
//        Assert.assertFalse(games.contains(gameId2));
//    }
//
////    @Test
////    public void testGetFavoriteGamesFromUser() {
////        Long userId = userSetUp();
////        Long gameId1 = insertGame(1L, SUGGESTED, 0, 0);
////        Long gameId2 = insertGame(2L, SUGGESTED, 0, 0);
////
////        Map<String, Object> args = new HashMap<>();
////        args.put("userid", userId);
////        args.put("gameid", gameId1);
////        insertForFavGames.execute(args);
////
////        List<Game> games = gameDao.getFavoriteGamesFromUser(userId);
////
////        Assert.assertEquals(1, games.size());
////        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1)));
////        Assert.assertFalse(games.stream().anyMatch(g -> g.getId().equals(gameId2)));
////    }
//
//    @Test
//    public void testDeleteFavoriteGameForUser() {
//        Long userId = userSetUp();
//        Long gameId1 = insertGame(1L, SUGGESTED, 0, 0);
//        Long gameId2 = insertGame(2L, SUGGESTED, 0, 0);
//        Map<String, Object> args = new HashMap<>();
//        args.put("userid", userId);
//        args.put("gameid", gameId1);
//        insertForFavGames.execute(args);
//
//        args.put("gameid", gameId2);
//        insertForFavGames.execute(args);
//
//
//        gameDao.deleteFavoriteGameForUser(userId, gameId1);
//        em.flush();
//
//        List<Long> gameIds = jdbcTemplate.queryForList("Select gameid from favoritegames", Long.class);
//        Assert.assertEquals(1, gameIds.size());
//        Assert.assertTrue(gameIds.contains(gameId2));
//    }
//
//    @Test
//    public void testGetFavoriteGamesCandidates() {
//        Long userId = userSetUp();
//        Long gameId1 = insertGame(1L, SUGGESTED, 0, 0);
//        Long gameId2 = insertGame(2L, SUGGESTED, 0, 0);
//        Long reviewId1 = reviewSetUp(1L, userId, gameId1, 8);
//
//        List<Game> games = gameDao.getFavoriteGamesCandidates(userId, 7);
//
//        Assert.assertEquals(1, games.size());
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1)));
//        Assert.assertFalse(games.stream().anyMatch(g -> g.getId().equals(gameId2)));
//    }
//
//    @Test
//    public void testGetFavoriteGamesCandidatesEmpty() {
//        Long userId = userSetUp();
//        Long gameId1 = insertGame(1L, SUGGESTED, 0, 0);
//        Long gameId2 = insertGame(2L, SUGGESTED, 0, 0);
//        Long reviewId1 = reviewSetUp(1L, userId, gameId1, 5);
//        Long reviewId2 = reviewSetUp(2L, userId, gameId2, 3);
//
//        List<Game> games = gameDao.getFavoriteGamesCandidates(userId, 7);
//
//        Assert.assertTrue(games.isEmpty());
//    }
//
//    @Test
//    public void testGetGenresByGame() {
//        //Setup game
//        Long gameId = insertGame(1L, SUGGESTED, 0, 0);
//        Map<String, Object> args = new HashMap<>();
//        args.put("gameid", gameId);
//        args.put("genreid", GENRE1.getId());
//        insertForGenres.execute(args);
//        args.put("genreid", GENRE2.getId());
//        insertForGenres.execute(args);
//
//        List<Genre> genres = gameDao.getGenresByGame(gameId);
//
//        Assert.assertEquals(2, genres.size());
//        Assert.assertTrue(genres.stream().anyMatch(g -> g.getId() == GENRE1.getId()));
//        Assert.assertTrue(genres.stream().anyMatch(g -> g.getId()==GENRE2.getId()));
//
//    }
//
//    @Test
//    public void testFindAllNoFiltersAverageRatingOrdering() {
//        Long gameId1 = insertGame(1L, SUGGESTED, 10, 1);
//        Long gameId2 = insertGame(2L, SUGGESTED, 5, 2);
//        GameFilterBuilder filterBuilder = new GameFilterBuilder();
//        Ordering<GameOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING);
//        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), ordering).getList();
//
//        Assert.assertEquals(2, games.size());
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1)));
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId2)));
//        Optional<Game> game = games.stream().findFirst();
//        Assert.assertTrue(game.isPresent());
//        Game game1 = game.get();
//        Assert.assertEquals(gameId1, game1.getId());
//    }
//
//    @Test
//    public void testFindAllGenreFilters() {
//        Long gameId1 = insertGame(1L, SUGGESTED, 10, 1);
//        Long gameId2 = insertGame(2L, SUGGESTED, 5, 2);
//        Map<String, Object> args = new HashMap<>();
//        args.put("gameid", gameId1);
//        args.put("genreid", GENRE1.getId());
//        insertForGenres.execute(args);
//        args.put("genreid", GENRE2.getId());
//        insertForGenres.execute(args);
//        args.put("gameid", gameId2);
//        insertForGenres.execute(args);
//        args.put("genreid", GENRE3.getId());
//        insertForGenres.execute(args);
//
//        GameFilterBuilder filterBuilder = new GameFilterBuilder().withGameGenres(Arrays.asList(GENRE1.getId(), GENRE2.getId()));
//        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();
//
//        Assert.assertEquals(2,games.size());
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1)));
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId2)));
//    }
//
//    @Test
//    public void testFindAllGenreFiltersEmpty() {
//        Long gameId1 = insertGame(1L, SUGGESTED, 10, 1);
//        Long gameId2 = insertGame(2L, SUGGESTED, 5, 2);
//        Map<String, Object> args = new HashMap<>();
//        args.put("gameid", gameId1);
//        args.put("genreid", GENRE1.getId());
//        insertForGenres.execute(args);
//        args.put("genreid", GENRE2.getId());
//        insertForGenres.execute(args);
//        args.put("gameid", gameId2);
//        insertForGenres.execute(args);
//
//        GameFilterBuilder filterBuilder = new GameFilterBuilder().withGameGenres(Arrays.asList(GENRE3.getId()));
//        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();
//
//        Assert.assertTrue(games.isEmpty());
//    }
//
//    @Test
//    public void testFindAllSearch() {
//        Long gameId1 = insertGame(1L, SUGGESTED, 10, 1);
//        Long gameId2 = insertGame(2L, SUGGESTED, 5, 2);
//        Long gameId3 = insertGame(3L, SUGGESTED, 3, 3);
//        GameFilterBuilder filterBuilder = new GameFilterBuilder().withGameContent(NAME);
//        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();
//
//        Assert.assertEquals(3, games.size());
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1)));
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId2)));
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId3)));
//    }
//
//    @Test
//    public void testFindAllSearchEmpty() {
//        Long gameId1 = insertGame(1L, SUGGESTED, 10, 1);
//        Long gameId2 = insertGame(2L, SUGGESTED, 5, 2);
//        Long gameId3 = insertGame(3L, SUGGESTED, 3, 3);
//        GameFilterBuilder filterBuilder = new GameFilterBuilder().withGameContent("NOTHING HERE");
//        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();
//
//        Assert.assertTrue(games.isEmpty());
//    }
//
//    @Test
//    public void testFindAllDeveloper() {
//        Long gameId1 = insertGame(1L, SUGGESTED, 10, 1);
//        Long gameId2 = insertGame(2L, SUGGESTED, 5, 2);
//
//        GameFilterBuilder filterBuilder = new GameFilterBuilder().withDeveloper(DEVELOPER);
//        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();
//
//        Assert.assertEquals(2, games.size());
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1)));
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId2)));
//    }
//
//    @Test
//    public void testFindAllPublishers() {
//        Long gameId1 = insertGame(1L, SUGGESTED, 10, 1);
//        Long gameId2 = insertGame(2L, SUGGESTED, 5, 2);
//
//        GameFilterBuilder filterBuilder = new GameFilterBuilder().withPublisher(PUBLISHER);
//        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();
//
//        Assert.assertEquals(2, games.size());
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1)));
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId2)));
//    }
//
//    @Test
//    public void testFindAllSearchSubString(){
//        Long gameId1 = insertGame(1L, SUGGESTED, 10, 1);
//
//        GameFilterBuilder filterBuilder = new GameFilterBuilder().withGameContent(NAME.substring(0,3));
//        List<Game> games = gameDao.findAll(Page.with(1,10),filterBuilder.build(),new Ordering<>(OrderDirection.DESCENDING,GameOrderCriteria.AVERAGE_RATING)).getList();
//
//        Assert.assertEquals(1,games.size());
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1)));
//    }
//
//
//    @Test
//    public void testFindAllRatingRange() {
//        Long gameId1 = insertGame(1L, SUGGESTED, 10, 1);
//        Long gameId2 = insertGame(2L, SUGGESTED, 16, 2);
//        Long gameId3 = insertGame(3L, SUGGESTED, 3, 3);
//        GameFilterBuilder filterBuilder = new GameFilterBuilder().withRatingRange(8.0f, 10.0f, false);
//        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();
//
//        Assert.assertEquals(2, games.size());
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1)));
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId2)));
//    }
//
//    @Test
//    public void testFindAllRatingRangeEmpty() {
//        Long gameId1 = insertGame(1L, SUGGESTED, 17, 2);
//        Long gameId2 = insertGame(2L, SUGGESTED, 16, 2);
//        Long gameId3 = insertGame(3L, SUGGESTED, 3, 3);
//        GameFilterBuilder filterBuilder = new GameFilterBuilder().withRatingRange(9.5f, 9.9f, false);
//        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();
//
//        Assert.assertTrue(games.isEmpty());
//    }
//
//    @Test
//    public void testGetRecommendationsForUser() {
//        //Setup game
//        Long gameId1 = insertGame(1L, SUGGESTED, 0, 0);
//        Long gameId2 = insertGame(2L, SUGGESTED, 0, 0);
//        //Setup genres
//        Map<String, Object> args = new HashMap<>();
//        args.put("gameid", gameId1);
//        args.put("genreid", GENRE1.getId());
//        insertForGenres.execute(args);
//        args.put("genreid", GENRE2.getId());
//        insertForGenres.execute(args);
//        args.put("gameid", gameId2);
//        insertForGenres.execute(args);
//        args.put("genreid", GENRE3.getId());
//        insertForGenres.execute(args);
//        //Setup user
//        List<Integer> genres = Arrays.asList(GENRE2.getId(), GENRE3.getId());
//
//        List<Game> games = gameDao.getRecommendationsForUser(genres,new ArrayList<>());
//
//        Assert.assertEquals(2,games.size());
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId1) ));
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId2) ));
//    }
//
//    @Test
//    public void testGetRecommendationsForUserExclude() {
//        //Setup game
//        Long gameId1 = insertGame(1L, SUGGESTED, 0, 0);
//        Long gameId2 = insertGame(2L, SUGGESTED, 0, 0);
//        //Setup genres
//        Map<String, Object> args = new HashMap<>();
//        args.put("gameid", gameId1);
//        args.put("genreid", GENRE1.getId());
//        insertForGenres.execute(args);
//        args.put("genreid", GENRE2.getId());
//        insertForGenres.execute(args);
//        args.put("gameid", gameId2);
//        insertForGenres.execute(args);
//        args.put("genreid", GENRE3.getId());
//        insertForGenres.execute(args);
//        //Setup user
//        List<Integer> genres = Arrays.asList(GENRE2.getId(), GENRE3.getId());
//
//        List<Game> games = gameDao.getRecommendationsForUser(genres,Arrays.asList(gameId1));
//
//        Assert.assertEquals(1,games.size());
//        Assert.assertFalse(games.stream().anyMatch(g -> g.getId().equals(gameId1) ));
//        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId2) ));
//    }
//
////    @Test
////    public void testGetGamesReviewdByUser(){
////        Long gameId = insertGame(1L, SUGGESTED, 0, 0);
////        Long userId = userSetUp();
////        Long reviewid = reviewSetUp(1L, userId, gameId, 1);
////
////        Set<Game> games = gameDao.getGamesReviewedByUser(userId);
////
////        Assert.assertEquals(1,games.size());
////        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(gameId)));
////
////    }
////
////    @Test
////    public void testGetGamesReviewdByUserEmpty(){
////        Long gameId = insertGame(1L, SUGGESTED, 0, 0);
////        Long userId = userSetUp();
////
////        Set<Game> games = gameDao.getGamesReviewedByUser(userId);
////
////        Assert.assertTrue(games.isEmpty());
////
////    }


}
