package ar.edu.itba.paw.persistence.tests;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.GameFilter;
import ar.edu.itba.paw.dtos.filtering.GameFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.helpers.CommonRowMappers;
import ar.edu.itba.paw.persistence.tests.utils.GameTestModels;
import ar.edu.itba.paw.persistence.tests.utils.UserTestModels;
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

    private static final int RATING_VALUE = 7;

    private static final String SEARCH_VALUE = "B";

    private static final String NOSEARCH = "NO HAY VALOR DE SEARCH";

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
    private Game testGameWithReview;
    private Game testGameSuggestedTrue;
    private User withFavoriteGamesUser;

    private User withNoReviewsCandidatesUser;
    private User withReviewsCandidatesUser;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        testGameNoGenres = GameTestModels.getSubnautica();
        testGame = GameTestModels.getSuperGameA();
        createGame = GameTestModels.getCreateGame();
        createGameNoGenres = GameTestModels.getCreateGameNoGenres();
        testGameWithReview = GameTestModels.getSubnautica();
        testGameSuggestedTrue = GameTestModels.getSubnautica();
        withFavoriteGamesUser = UserTestModels.getUser5();
        withNoReviewsCandidatesUser = UserTestModels.getUser4();
        withReviewsCandidatesUser = UserTestModels.getUser2();
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
                createGame.getSuggestion(),
                null);
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
                createGameNoGenres.getSuggestion(),
                null);
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

    @Rollback
    @Test
    public void testModifyReview() {
        gameDao.modifyReview(testGameWithReview.getId(), 1, RATING_VALUE);
        em.flush();
        Object[] params = {testGameWithReview.getId()};
        Game dbGame = jdbcTemplate.queryForObject("Select * from games where id = ?", params, CommonRowMappers.TEST_GAME_ROW_MAPPER);
        Assert.assertNotNull(dbGame);
        double expectedRating = (double) (testGameWithReview.getRatingSum() - 1 + RATING_VALUE) / testGameWithReview.getReviewCount();
        Assert.assertEquals(expectedRating, dbGame.getAverageRating(), 0.001);
    }

    @Rollback
    @Test
    public void testDeleteReview() {
        gameDao.deleteReviewFromGame(testGameWithReview.getId(), 1);
        em.flush();
        Object[] params = {testGameWithReview.getId()};
        Game games = jdbcTemplate.queryForObject("Select * from games where id = ?", params, CommonRowMappers.TEST_GAME_ROW_MAPPER);
        Assert.assertNotNull(games);
        double expectedRating = (double) (testGameWithReview.getRatingSum() - 1) / (testGameWithReview.getReviewCount() - 1);
        Assert.assertEquals(expectedRating,
                games.getAverageRating(), 0.001);
    }

    @Rollback
    @Test
    public void testSetSuggestedFalse(){
        Optional<Game> game = gameDao.setSuggestedFalse(testGameSuggestedTrue.getId());
        em.flush();

        Assert.assertTrue(game.isPresent());
        Boolean suggested = jdbcTemplate.queryForObject("Select suggestion from games where games.name = ?",Boolean.class, testGameSuggestedTrue.getName());
        Assert.assertEquals(false,suggested);
    }

    @Rollback
    @Test
    public void testSetSuggestedFalseNotFound(){
        Optional<Game> gameResult = gameDao.setSuggestedFalse(-1L);

        Assert.assertFalse(gameResult.isPresent());
    }

    @Rollback
    @Test
    public void testDeleteGame(){
        boolean response = gameDao.deleteGame(testGame.getId());
        em.flush();
        Assert.assertTrue(response);
        Assert.assertEquals(0,JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,"games",String.format("id = %d",testGame.getId())));
    }

    @Rollback
    @Test
    public void testDeleteGameNotFound(){

        boolean response = gameDao.deleteGame(-1L);
        em.flush();
        Assert.assertFalse(response);
    }



    @Rollback
    @Test
    public void testGetFavoriteGamesFromUser() {
        Optional<List<Game>> games = gameDao.getFavoriteGamesFromUser(withFavoriteGamesUser.getId());

        Assert.assertTrue(games.isPresent());
        Assert.assertEquals(3, games.get().size());

        List<Game> expectedFavorites = Arrays.asList(GameTestModels.getSuperGameA(), GameTestModels.getSuperGameB(), GameTestModels.getSubnautica());
        Assert.assertArrayEquals(expectedFavorites.toArray(), games.get().toArray());
    }


    @Rollback
    @Test
    public void testGetFavoriteGamesCandidates() {

        List<Game> games = gameDao.getFavoriteGamesCandidates(withReviewsCandidatesUser.getId(), 7);

        Assert.assertEquals(1, games.size());
        Assert.assertTrue(games.stream().anyMatch(g -> g.getId().equals(GameTestModels.getSuperGameB().getId())));
    }

    @Rollback
    @Test
    public void testGetFavoriteGamesCandidatesEmpty() {
        List<Game> games = gameDao.getFavoriteGamesCandidates(withNoReviewsCandidatesUser.getId(), 7);

        Assert.assertTrue(games.isEmpty());
    }

    @Test
    public void testGetGenresByGame() {
        Game gameWithGenres = GameTestModels.getGameWithGenres();
        //Setup game
        List<Genre> genres = gameDao.getGenresByGame(gameWithGenres.getId());

        Assert.assertEquals(2, genres.size());
        List<Genre> expectedGenres = Arrays.asList(Genre.ACTION, Genre.ADVENTURE);

        Assert.assertArrayEquals(expectedGenres.toArray(), genres.toArray());
    }

    @Rollback
    @Test
    public void testFindAllNoFiltersAverageRatingOrdering() {
        GameFilterBuilder filterBuilder = new GameFilterBuilder();
        Ordering<GameOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING);
        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), ordering).getList();

        Assert.assertEquals(4, games.size());
        List<Game> expectedGames = Arrays.asList(GameTestModels.getSuperGameB(), GameTestModels.getSubnautica(), GameTestModels.getSubnautica2(), GameTestModels.getSuperGameA());
        Assert.assertArrayEquals(expectedGames.toArray(), games.toArray());
    }

    @Rollback
    @Test
    public void testFindAllGenreFilters() {
        GameFilterBuilder filterBuilder = new GameFilterBuilder().withGameGenres(Arrays.asList(Genre.ACTION.getId(), Genre.ADVENTURE.getId()));
        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();

        Assert.assertEquals(2, games.size());
        List<Game> expectedGames = Arrays.asList(GameTestModels.getSuperGameB(), GameTestModels.getSuperGameA());
        Assert.assertArrayEquals(expectedGames.toArray(), games.toArray());
    }

    @Rollback
    @Test
    public void testFindAllGenreFiltersEmpty() {
        GameFilterBuilder filterBuilder = new GameFilterBuilder().withGameGenres(Collections.singletonList(Genre.MMO.getId()));
        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();

        Assert.assertTrue(games.isEmpty());
    }

    @Rollback
    @Test
    public void testFindAllSearch() {
        GameFilterBuilder filterBuilder = new GameFilterBuilder().withGameContent(SEARCH_VALUE);
        List<Game> games = gameDao.findAll(Page.with(1, 10),
                filterBuilder.build(),
                new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.NAME)).getList();

        Assert.assertEquals(3, games.size());
        List<Game> expectedGames = Arrays.asList(GameTestModels.getSuperGameB(), GameTestModels.getSubnautica2(), GameTestModels.getSubnautica());
        Assert.assertArrayEquals(expectedGames.toArray(), games.toArray());
    }

    @Rollback
    @Test
    public void testFindAllSearchEmpty() {
        GameFilterBuilder filterBuilder = new GameFilterBuilder().withGameContent(NOSEARCH);
        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();

        Assert.assertTrue(games.isEmpty());
    }

    @Rollback
    @Test
    public void testFindAllDeveloper() {
        GameFilterBuilder filterBuilder = new GameFilterBuilder().withDeveloper(testGame.getDeveloper());
        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();

        Assert.assertEquals(4, games.size());
    }

    @Test
    public void testFindAllPublishers() {
        GameFilterBuilder filterBuilder = new GameFilterBuilder().withPublisher(testGame.getPublisher());
        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();

        Assert.assertEquals(4, games.size());
    }

    @Rollback
    @Test
    public void testFindAllRatingRange() {
        GameFilterBuilder filterBuilder = new GameFilterBuilder().withRatingRange(8.0f, 10.0f, false);
        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();

        Assert.assertEquals(1, games.size());
        List<Game> expectedGames = Collections.singletonList(GameTestModels.getSuperGameB());
        Assert.assertArrayEquals(expectedGames.toArray(), games.toArray());
    }

    @Rollback
    @Test
    public void testFindAllRatingRangeEmpty() {
        GameFilterBuilder filterBuilder = new GameFilterBuilder().withRatingRange(9.5f, 9.9f, false);
        List<Game> games = gameDao.findAll(Page.with(1, 10), filterBuilder.build(), new Ordering<>(OrderDirection.DESCENDING, GameOrderCriteria.AVERAGE_RATING)).getList();

        Assert.assertTrue(games.isEmpty());
    }

    @Rollback
    @Test
    public void testGetRecommendationsForUser() {
        //Setup user
        List<Integer> genres = Collections.singletonList(Genre.CASUAL.getId());

        List<Game> games = gameDao.getRecommendationsForUser(genres,new ArrayList<>());

        Assert.assertEquals(1, games.size());
        Game[] expectedGames = {GameTestModels.getSuperGameB()};
        Assert.assertArrayEquals(expectedGames, games.toArray());
    }

    @Rollback
    @Test
    public void testGetRecommendationsForUserExclude() {
        List<Game> games = gameDao.getRecommendationsForUser(Collections.singletonList(Genre.ADVENTURE.getId()),
                Collections.singletonList(GameTestModels.getSuperGameB().getId()));

        Assert.assertEquals(1,games.size());
        List<Game> expectedGames = Collections.singletonList(GameTestModels.getSuperGameA());
        Assert.assertArrayEquals(expectedGames.toArray(), games.toArray());
    }

    @Rollback
    @Test
    public void testGetGamesReviewedByUser(){
        Optional<Set<Game>> games = gameDao.getGamesReviewedByUser(UserTestModels.getUser2().getId());

        Assert.assertTrue(games.isPresent());
        Assert.assertEquals(2,games.get().size());
        List<Game> expectedGames = Arrays.asList(GameTestModels.getSuperGameB(), GameTestModels.getSubnautica());
        Assert.assertArrayEquals(expectedGames.toArray(), games.get().toArray());
    }

    @Test
    public void testGetGamesReviewedByUserEmpty(){
        Optional<Set<Game>> games = gameDao.getGamesReviewedByUser(UserTestModels.getUser4().getId());
        Assert.assertTrue(games.isPresent());
        Assert.assertTrue(games.get().isEmpty());
    }

    @Test
    public void testGetGamesReviewedByNonExistentUser() {
        Optional<Set<Game>> games = gameDao.getGamesReviewedByUser(1000);
        Assert.assertFalse(games.isPresent());
    }
}
