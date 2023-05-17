package ar.edu.itba.paw.persistence.tests;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.SaveReviewDTO;
import ar.edu.itba.paw.dtos.filtering.ReviewFilter;
import ar.edu.itba.paw.dtos.filtering.ReviewFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.enums.ReviewFeedback;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.ReviewDaoImpl;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.helpers.CommonRowMappers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ReviewDaoImplTest {

    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsertForReviews;
    private SimpleJdbcInsert jdbcInsertForReviewFeedback;
    @Autowired
    private ReviewDaoImpl reviewDao;

    //Review fields
    private static final String TITLE = "title";
    private static final String CONTENT = "review text";
    private static final Timestamp TIMESTAMP = new Timestamp(System.currentTimeMillis());
    private static final Integer RATING = 10;
    private static final Difficulty DIFFICULTY = Difficulty.HARD;
    private static final Platform PLATFORM = Platform.PC;
    private static final Double GAME_LENGTH = 100.78;
    private static final Boolean REPLAYABILITY = true;
    private static final Boolean COMPLETED = false;
    private static final Long LIKES = 4L;
    private static final Long DISLIKES = 2L;
    private static final ReviewFeedback REVIEW_FEEDBACK = ReviewFeedback.LIKE;

    //Game fields
    private Number firstGame = 1L;
    private Number secondGame = 2L;
    private static final String NAME =  "game name";
    private static final String DESCRIPTION = "game description";
    private static final String DEVELOPER = "game developer";
    private static final String PUBLISHER = "game publisher";
    private static final Boolean SUGGESTED = false;

    //User fields
    private Number firstUser = 1L;
    private Number secondUser = 2L;
    private static final String USERNAME1 = "username1";
    private static final String USERNAME2 = "username2";
    private static final String PASSWORD = "password";
    private static final String EMAIL1 = "email";
    private static final String EMAIL2 = "email2";
    private static final boolean ENABLED = true;

    //Genres
    private static final Long GENRE_ID1 = 1L;
    private static final Long GENRE_ID2 = 2L;
    private static final Long GENRE_ID3 = 3L;

    //Images
    private static final String IMAGE_ID = "id1";
    private static final byte[] IMAGE = "E".getBytes();
    private static final String MEDIA_TYPE = "image/jpeg";


    @Before
    public void setUp(){
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsertForReviews = new SimpleJdbcInsert(ds).withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        this.jdbcInsertForReviewFeedback = new SimpleJdbcInsert(ds).withTableName("reviewfeedback");
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"reviews");
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"reviewfeedback");
    }

    @Before
    public void globalsetUp(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"users");
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"games");
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"images");
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"genreforusers");
        JdbcTestUtils.deleteFromTables(jdbcTemplate,"genreforgames");

        SimpleJdbcInsert jdbcInsertForUsers = new SimpleJdbcInsert(ds).withTableName("users")
                .usingGeneratedKeyColumns("id");
        SimpleJdbcInsert jdbcInsertForGames = new SimpleJdbcInsert(ds).withTableName("games")
                .usingGeneratedKeyColumns("id");
        SimpleJdbcInsert jdbcInsertForGameGenres = new SimpleJdbcInsert(ds).withTableName("genreforgames");
        SimpleJdbcInsert jdbcInsertForImages = new SimpleJdbcInsert(ds).withTableName("images");
        SimpleJdbcInsert jdbcInsertForUsersGenres = new SimpleJdbcInsert(ds).withTableName("genreforusers");
        Map<String,Object> args = new HashMap<>();

        //Setup users
        args.put("username",USERNAME1);
        args.put("password",PASSWORD);
        args.put("email",EMAIL1);
        args.put("enabled",ENABLED);
        args.put("reputation",0L);
        firstUser = jdbcInsertForUsers.executeAndReturnKey(args);
        args.put("username",USERNAME2);
        args.put("email",EMAIL2);
        secondUser = jdbcInsertForUsers.executeAndReturnKey(args);

        //Setup images
        args.clear();
        args.put("data",IMAGE);
        args.put("id",IMAGE_ID);
        args.put("mediaType",MEDIA_TYPE);
        jdbcInsertForImages.execute(args);
        //Setup games
        args.clear();
        args.put("name",NAME);
        args.put("description",DESCRIPTION);
        args.put("developer",DEVELOPER);
        args.put("publisher",PUBLISHER);
        args.put("publishdate",TIMESTAMP);
        args.put("suggested",SUGGESTED);
        args.put("image",IMAGE_ID);
        firstGame = jdbcInsertForGames.executeAndReturnKey(args);
        secondGame = jdbcInsertForGames.executeAndReturnKey(args);

        //Setup genres
        args.clear();
        args.put("gameid",firstGame);
        args.put("genreid",GENRE_ID1);
        jdbcInsertForGameGenres.execute(args);

        args.put("genreid",GENRE_ID2);
        jdbcInsertForGameGenres.execute(args);
        args.put("gameid",secondGame);
        jdbcInsertForGameGenres.execute(args);
        args.put("genreid",GENRE_ID3);
        jdbcInsertForGameGenres.execute(args);
        //Setup user preferences
        args.clear();

        args.put("userid",firstUser);
        args.put("genreid",GENRE_ID1);
        jdbcInsertForUsersGenres.execute(args);
        args.put("genreid",GENRE_ID2);
        jdbcInsertForUsersGenres.execute(args);
        args.put("userid",secondUser);
        jdbcInsertForUsersGenres.execute(args);
        args.put("genreid",GENRE_ID3);
        jdbcInsertForUsersGenres.execute(args);

    }

    private Number addReviewRow(long authorid, long gameid, String title, String content,
                              Timestamp createddate, long rating, String difficulty, String platform,
                              double gamelength, boolean replayability, boolean completed, long likes, long dislikes) {
        Map<String,Object> args = new HashMap<>();
        args.put("authorid",authorid);
        args.put("gameid",gameid);
        args.put("title",title);
        args.put("content",content);
        args.put("createddate",createddate);
        args.put("rating",rating);
        args.put("difficulty",difficulty);
        args.put("platform",platform);
        args.put("gamelength",gamelength);
        args.put("replayability",replayability);
        args.put("completed",completed);
        args.put("likes",likes);
        args.put("dislikes",dislikes);
        return jdbcInsertForReviews.executeAndReturnKey(args);
    }

    private void likeReview(long reviewid, long userid, boolean like) {
        Map<String,Object> args = new HashMap<>();
        args.put("reviewid",reviewid);
        args.put("userid",userid);
        args.put("feedback",like? "LIKE": "DISLIKE");
        jdbcInsertForReviewFeedback.execute(args);
    }

    @Test
    public void testFindByIDNoReviewFeedback(){
        //Setup
        Number reviewId = addReviewRow(firstUser.longValue(),firstGame.longValue(),TITLE,CONTENT,TIMESTAMP,RATING,DIFFICULTY.toString(),PLATFORM.toString(),GAME_LENGTH,REPLAYABILITY,COMPLETED,LIKES,DISLIKES);

        //Test
        Optional<Review> review = reviewDao.findById(reviewId.longValue(),firstUser.longValue());

        Assert.assertTrue(review.isPresent());
        Assert.assertEquals(review.get().getId(),Long.valueOf(reviewId.longValue()));
        Assert.assertEquals(review.get().getAuthor().getUsername(),USERNAME1);
        Assert.assertEquals(review.get().getGameLength(), GAME_LENGTH);
        Assert.assertNull(review.get().getFeedback());
    }

    @Test
    public void testFindByIDNoActiveUser(){
        // Setup
        Number reviewId = addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        likeReview(reviewId.longValue(), firstUser.longValue(), true);

        // Test
        Optional<Review> review = reviewDao.findById(reviewId.longValue(), null);

        Assert.assertTrue(review.isPresent());
        Assert.assertEquals(review.get().getId(), Long.valueOf(reviewId.longValue()));
        Assert.assertEquals(review.get().getAuthor().getUsername(), USERNAME1);
        Assert.assertNull(review.get().getFeedback());
    }

    @Test
    public void testFindByIDNonexistent(){
        // Test
        Optional<Review> review = reviewDao.findById(999L, firstUser.longValue());

        Assert.assertFalse(review.isPresent());
    }

    @Test
    public void testFindByIDWithReviewFeedback(){
        // Setup
        Number reviewId = addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        likeReview(reviewId.longValue(), firstUser.longValue(), true);

        // Test
        Optional<Review> review = reviewDao.findById(reviewId.longValue(), firstUser.longValue());

        Assert.assertTrue(review.isPresent());
        Assert.assertEquals(review.get().getId(), Long.valueOf(reviewId.longValue()));
        Assert.assertEquals(review.get().getAuthor().getId(), Long.valueOf(firstUser.longValue()));
        Assert.assertEquals(review.get().getFeedback(), ReviewFeedback.LIKE);
    }

    @Test
    public void testUpdateReviewSuccess() {
        // Setup
        Number reviewId = addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        SaveReviewDTO updatedReviewDTO = new SaveReviewDTO("Updated Title", "Updated Content", 4, Difficulty.EASY, 60.6, Platform.PC, true, false);

        // Test
        int result = reviewDao.update(reviewId.longValue(), updatedReviewDTO);

        Assert.assertEquals(result, 1);

        // Verify the updated values
        Optional<Review> updatedReview = jdbcTemplate.query("SELECT * FROM reviews JOIN games as g ON g.id = reviews.gameid JOIN users as u ON u.id = reviews.authorid WHERE reviews.id = ?", CommonRowMappers.REVIEW_ROW_MAPPER, reviewId.longValue()).stream().findFirst();
        Assert.assertTrue(updatedReview.isPresent());
        Assert.assertEquals(updatedReview.get().getTitle(), "Updated Title");
        Assert.assertEquals(updatedReview.get().getContent(), "Updated Content");
        Assert.assertEquals(updatedReview.get().getRating(), Integer.valueOf(4));
        Assert.assertEquals(updatedReview.get().getDifficulty(), Difficulty.EASY);
        Assert.assertTrue(updatedReview.get().getCompleted());
        Assert.assertFalse(updatedReview.get().getReplayability());
        Assert.assertEquals(updatedReview.get().getPlatform(), Platform.PC);
        Assert.assertEquals(updatedReview.get().getGameLength(), Double.valueOf(60.6));
    }

    @Test
    public void testUpdateNonExistentReview() {
        // Setup
        Long nonExistentReviewId = 999L;
        SaveReviewDTO updatedReviewDTO = new SaveReviewDTO("Updated Title", "Updated Content", 4, Difficulty.EASY, 60.6, Platform.PC, true, false);

        // Test
        int result = reviewDao.update(nonExistentReviewId, updatedReviewDTO);

        Assert.assertEquals(result, 0);
    }

    @Test
    public void testCreateReviewSuccess() {
        // Setup
        String title = TITLE;
        String content = CONTENT;
        Integer rating = RATING;
        Game reviewedGame = new Game(firstGame.longValue(), NAME, DESCRIPTION, DEVELOPER, PUBLISHER, "", new ArrayList<>(), LocalDate.now(), 7.3);
        User author = new User(firstUser.longValue(), USERNAME1, EMAIL1, PASSWORD);
        Difficulty difficulty = DIFFICULTY;
        Double gameLength = GAME_LENGTH;
        Platform platform = PLATFORM;
        Boolean completed = COMPLETED;
        Boolean replayable = REPLAYABILITY;

        // Test
        Review createdReview = reviewDao.create(title, content, rating, reviewedGame, author, difficulty, gameLength, platform, completed, replayable);

        // Verify the created review
        Assert.assertNotNull(createdReview);
        Assert.assertEquals(createdReview.getTitle(), title);
        Assert.assertEquals(createdReview.getContent(), content);
        Assert.assertEquals(createdReview.getRating(), rating);
        Assert.assertEquals(createdReview.getReviewedGame(), reviewedGame);
        Assert.assertEquals(createdReview.getAuthor(), author);
        Assert.assertEquals(createdReview.getDifficulty(), difficulty);
        Assert.assertEquals(createdReview.getGameLength(), gameLength);
        Assert.assertEquals(createdReview.getPlatform(), platform);
        Assert.assertEquals(createdReview.getCompleted(), completed);
        Assert.assertEquals(createdReview.getReplayability(), replayable);

        Optional<Review> reviewFromDB = jdbcTemplate.query("SELECT * FROM reviews JOIN games as g ON g.id = reviews.gameid JOIN users as u ON u.id = reviews.authorid WHERE reviews.id = ?", CommonRowMappers.REVIEW_ROW_MAPPER, createdReview.getId()).stream().findFirst();
        Assert.assertTrue(reviewFromDB.isPresent());
        Review review = reviewFromDB.get();
        Assert.assertEquals(review.getTitle(), title);
        Assert.assertEquals(review.getContent(), content);
        Assert.assertEquals(review.getRating(), rating);
        Assert.assertEquals(review.getReviewedGame(), reviewedGame);
        Assert.assertEquals(review.getAuthor(), author);
        Assert.assertEquals(review.getDifficulty(), difficulty);
        Assert.assertEquals(review.getGameLength(), gameLength);
        Assert.assertEquals(review.getPlatform(), platform);
        Assert.assertEquals(review.getCompleted(), completed);
        Assert.assertEquals(review.getReplayability(), replayable);
    }

    @Test
    public void testCreateWithNullNullableValues() {
        // Setup
        String title = TITLE;
        String content = CONTENT;
        Integer rating = RATING;
        Game reviewedGame = new Game(firstGame.longValue(), NAME, DESCRIPTION, DEVELOPER, PUBLISHER, "", new ArrayList<>(), LocalDate.now(), 7.3);
        User author = new User(firstUser.longValue(), USERNAME1, EMAIL1, PASSWORD);
        Difficulty difficulty = null;
        Double gameLength = null;
        Platform platform = null;
        Boolean completed = null;
        Boolean replayable = null;

        // Test
        Review createdReview = reviewDao.create(title, content, rating, reviewedGame, author, difficulty, gameLength, platform, completed, replayable);

        // Verify the created review
        Assert.assertNotNull(createdReview);
        Assert.assertEquals(createdReview.getTitle(), title);
        Assert.assertEquals(createdReview.getContent(), content);
        Assert.assertEquals(createdReview.getRating(), rating);
        Assert.assertEquals(createdReview.getReviewedGame(), reviewedGame);
        Assert.assertEquals(createdReview.getAuthor(), author);
        Assert.assertNull(createdReview.getDifficulty());
        Assert.assertNull(createdReview.getGameLength());
        Assert.assertNull(createdReview.getPlatform());
        Assert.assertNull(createdReview.getCompleted());
        Assert.assertNull(createdReview.getReplayability());

        Optional<Review> reviewFromDB = jdbcTemplate.query("SELECT * FROM reviews JOIN games as g ON g.id = reviews.gameid JOIN users as u ON u.id = reviews.authorid WHERE reviews.id = ?", CommonRowMappers.REVIEW_ROW_MAPPER, createdReview.getId()).stream().findFirst();
        Assert.assertTrue(reviewFromDB.isPresent());
        Review review = reviewFromDB.get();
        Assert.assertEquals(review.getTitle(), title);
        Assert.assertEquals(review.getContent(), content);
        Assert.assertEquals(review.getRating(), rating);
        Assert.assertEquals(review.getReviewedGame(), reviewedGame);
        Assert.assertEquals(review.getAuthor(), author);
        Assert.assertNull(review.getDifficulty());
        Assert.assertNull(review.getGameLength());
        Assert.assertNull(review.getPlatform());
        Assert.assertFalse(review.getCompleted());
        Assert.assertFalse(review.getReplayability());
    }

    @Test
    public void testCreateNullTitle() {
        // Setup
        String title = null;
        Game reviewedGame = new Game(firstGame.longValue(), NAME, DESCRIPTION, DEVELOPER, PUBLISHER, "", new ArrayList<>(), LocalDate.now(), 7.3);
        User author = new User(firstUser.longValue(), USERNAME1, EMAIL1, PASSWORD);

        // Test
        Assert.assertThrows(DataIntegrityViolationException.class, () -> reviewDao.create(title, CONTENT, RATING, reviewedGame, author, DIFFICULTY, GAME_LENGTH, PLATFORM, COMPLETED, REPLAYABILITY));

    }

    @Test
    public void testFindAllReviewsWithTitle() {
        // Setup
        addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(firstUser.longValue(), secondGame.longValue(), "HOLA" + TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(secondUser.longValue(), firstGame.longValue(), "HOLA", CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Page pagination = Page.with(1,10);
        ReviewFilter filter = new ReviewFilterBuilder().withReviewContent(TITLE).build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_SCORE);
        Long activeUserId = null;

        // Test
        Paginated<Review> result = reviewDao.findAll(pagination, filter, ordering, activeUserId);

        // Assertions
        Assert.assertEquals(result.getPage(), 1);
        Assert.assertEquals(result.getPageSize(), 10);
        Assert.assertEquals(result.getTotalPages(), 1);
        Assert.assertEquals(result.getList().size(), 2);

        for (Review review : result.getList()) {
            Assert.assertTrue(review.getTitle().contains(TITLE));
        }
    }

    @Test
    public void testFindAllReviewsWithContent() {
        // Setup
        addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(firstUser.longValue(), secondGame.longValue(), TITLE, "AA" + CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(secondUser.longValue(), firstGame.longValue(), TITLE, "AA", TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Page pagination = Page.with(1,10);
        ReviewFilter filter = new ReviewFilterBuilder().withReviewContent(CONTENT).build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_SCORE);
        Long activeUserId = null;

        // Test
        Paginated<Review> result = reviewDao.findAll(pagination, filter, ordering, activeUserId);

        // Assertions
        Assert.assertEquals(result.getPage(), 1);
        Assert.assertEquals(result.getPageSize(), 10);
        Assert.assertEquals(result.getTotalPages(), 1);
        Assert.assertEquals(result.getList().size(), 2);

        for (Review review : result.getList()) {
            Assert.assertTrue(review.getContent().contains(CONTENT));
        }
    }

    @Test
    public void testFindAllReviewsWithAuthor() {
        // Setup
        addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(firstUser.longValue(), secondGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(secondUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Page pagination = Page.with(1,10);
        ReviewFilter filter = new ReviewFilterBuilder().withAuthors(Arrays.asList(firstUser.longValue())).build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_SCORE);
        Long activeUserId = null;

        // Test
        Paginated<Review> result = reviewDao.findAll(pagination, filter, ordering, activeUserId);

        // Assertions
        Assert.assertEquals(result.getPage(), 1);
        Assert.assertEquals(result.getPageSize(), 10);
        Assert.assertEquals(result.getTotalPages(), 1);
        Assert.assertEquals(result.getList().size(), 2);

        for (Review review : result.getList()) {
            Assert.assertEquals((long) review.getAuthor().getId(), firstUser.longValue());
        }
    }

    @Test
    public void testFindAllReviewsWithGame() {
        // Setup
        addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(firstUser.longValue(), secondGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(secondUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Page pagination = Page.with(1,10);
        ReviewFilter filter = new ReviewFilterBuilder().withGameId(firstGame.longValue()).build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_SCORE);
        Long activeUserId = null;

        // Test
        Paginated<Review> result = reviewDao.findAll(pagination, filter, ordering, activeUserId);

        // Assertions
        Assert.assertEquals(result.getPage(), 1);
        Assert.assertEquals(result.getPageSize(), 10);
        Assert.assertEquals(result.getTotalPages(), 1);
        Assert.assertEquals(result.getList().size(), 2);

        for (Review review : result.getList()) {
            Assert.assertEquals((long) review.getReviewedGame().getId(), firstGame.longValue());
        }
    }

    @Test
    public void testFindAllReviewsWithGameGenres() {
        // Setup
        addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(firstUser.longValue(), secondGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(secondUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Page pagination = Page.with(1,10);
        ReviewFilter filter = new ReviewFilterBuilder().withGameGenres(Arrays.asList(GENRE_ID1.intValue())).build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_SCORE);
        Long activeUserId = null;

        // Test
        Paginated<Review> result = reviewDao.findAll(pagination, filter, ordering, activeUserId);

        // Assertions
        Assert.assertEquals(result.getPage(), 1);
        Assert.assertEquals(result.getPageSize(), 10);
        Assert.assertEquals(result.getTotalPages(), 1);
        Assert.assertEquals(result.getList().size(), 2);

        for (Review review : result.getList()) {
            Assert.assertTrue(review.getReviewedGame().getGenres().stream().anyMatch(genre -> genre.getId() == GENRE_ID1));
        }
    }

    @Test
    public void testFindAllReviewsWithUserPreferences() {
        // Setup
        addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(firstUser.longValue(), secondGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(secondUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Page pagination = Page.with(1,10);
        ReviewFilter filter = new ReviewFilterBuilder().withAuthorGenres(Arrays.asList(GENRE_ID1.intValue())).build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_SCORE);
        Long activeUserId = null;

        // Test
        Paginated<Review> result = reviewDao.findAll(pagination, filter, ordering, activeUserId);

        // Assertions
        Assert.assertEquals(result.getPage(), 1);
        Assert.assertEquals(result.getPageSize(), 10);
        Assert.assertEquals(result.getTotalPages(), 1);
        Assert.assertEquals(result.getList().size(), 2);

        for (Review review : result.getList()) {
            Assert.assertTrue(review.getAuthor().getPreferences().stream().anyMatch(genre -> genre.getId() == GENRE_ID1));
        }
    }

    @Test
    public void testFindAllReviewsWithDifficulty() {
        // Setup
        addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(firstUser.longValue(), secondGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, Difficulty.EASY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(secondUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Page pagination = Page.with(1,10);
        ReviewFilter filter = new ReviewFilterBuilder().withDifficulties(Arrays.asList(Difficulty.MEDIUM, Difficulty.HARD)).build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_SCORE);
        Long activeUserId = null;

        // Test
        Paginated<Review> result = reviewDao.findAll(pagination, filter, ordering, activeUserId);

        // Assertions
        Assert.assertEquals(result.getPage(), 1);
        Assert.assertEquals(result.getPageSize(), 10);
        Assert.assertEquals(result.getTotalPages(), 1);
        Assert.assertEquals(result.getList().size(), 2);

        Assert.assertTrue(result.getList().stream().anyMatch(review -> review.getDifficulty() == Difficulty.MEDIUM || review.getDifficulty() == Difficulty.HARD));
    }

    @Test
    public void testFindAllReviewsWithPlatform() {
        // Setup
        addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(firstUser.longValue(), secondGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), Platform.PS.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(secondUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Page pagination = Page.with(1,10);
        ReviewFilter filter = new ReviewFilterBuilder().withPlatforms(Arrays.asList(Platform.PC, Platform.XBOX)).build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_SCORE);
        Long activeUserId = null;

        // Test
        Paginated<Review> result = reviewDao.findAll(pagination, filter, ordering, activeUserId);

        // Assertions
        Assert.assertEquals(result.getPage(), 1);
        Assert.assertEquals(result.getPageSize(), 10);
        Assert.assertEquals(result.getTotalPages(), 1);
        Assert.assertEquals(result.getList().size(), 2);

        Assert.assertTrue(result.getList().stream().anyMatch(review -> review.getPlatform() == Platform.PC || review.getPlatform() == Platform.XBOX));
    }

    @Test
    public void testFindAllReviewsOrder() {
        // Setup
        addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 5, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(firstUser.longValue(), secondGame.longValue(), TITLE, CONTENT, TIMESTAMP, 7, DIFFICULTY.toString(), Platform.PS.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(secondUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 2, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Page pagination = Page.with(1,10);
        ReviewFilter filter = new ReviewFilterBuilder().build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_SCORE);
        Long activeUserId = null;

        // Test
        Paginated<Review> result = reviewDao.findAll(pagination, filter, ordering, activeUserId);

        // Assertions
        Assert.assertEquals(result.getPage(), 1);
        Assert.assertEquals(result.getPageSize(), 10);
        Assert.assertEquals(result.getTotalPages(), 1);
        Assert.assertEquals(result.getList().size(), 3);

        Assert.assertEquals(result.getList().get(0).getRating(), Integer.valueOf(7));
        Assert.assertEquals(result.getList().get(1).getRating(), Integer.valueOf(5));
        Assert.assertEquals(result.getList().get(2).getRating(), Integer.valueOf(2));

    }

    @Test
    public void testFindAllReviewsFeedback() {
        // Setup
        Number r1 = addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 1, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Number r2 = addReviewRow(firstUser.longValue(), secondGame.longValue(), TITLE, CONTENT, TIMESTAMP, 2, DIFFICULTY.toString(), Platform.PS.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Number r3 = addReviewRow(secondUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 3, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        likeReview(r1.longValue(), firstUser.longValue(), true);
        likeReview(r2.longValue(), firstUser.longValue(), false);
        likeReview(r3.longValue(), secondUser.longValue(), true);

        Page pagination = Page.with(1,10);
        ReviewFilter filter = new ReviewFilterBuilder().build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.ASCENDING, ReviewOrderCriteria.REVIEW_SCORE);
        Long activeUserId = firstUser.longValue();

        // Test
        Paginated<Review> result = reviewDao.findAll(pagination, filter, ordering, activeUserId);

        // Assertions
        Assert.assertEquals(result.getPage(), 1);
        Assert.assertEquals(result.getPageSize(), 10);
        Assert.assertEquals(result.getTotalPages(), 1);
        Assert.assertEquals(result.getList().size(), 3);

        Assert.assertEquals(result.getList().get(0).getFeedback(), ReviewFeedback.LIKE);
        Assert.assertEquals(result.getList().get(1).getFeedback(), ReviewFeedback.DISLIKE);
        Assert.assertNull(result.getList().get(2).getFeedback());

    }

    @Test
    public void testFindAllReviewsPagination() {
        // Setup
        for(int i = 0; i < 20; i++) {
            addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        }

        Page pagination = Page.with(3,5);
        ReviewFilter filter = new ReviewFilterBuilder().build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.ASCENDING, ReviewOrderCriteria.REVIEW_SCORE);
        Long activeUserId = firstUser.longValue();

        // Test
        Paginated<Review> result = reviewDao.findAll(pagination, filter, ordering, activeUserId);

        // Assertions
        Assert.assertEquals(result.getPage(), 3);
        Assert.assertEquals(result.getPageSize(), 5);
        Assert.assertEquals(result.getTotalPages(), 4);
        Assert.assertEquals(result.getList().size(), 5);
    }

    @Test
    public void testNoPaginatingFindAllReviews() {
        // Setup
        for(int i = 0; i < 200; i++) {
            Number reviewId = addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
            likeReview(reviewId.longValue(), firstUser.longValue(), true);
        }

        ReviewFilter filter = new ReviewFilterBuilder().build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.ASCENDING, ReviewOrderCriteria.REVIEW_SCORE);
        Long activeUserId = firstUser.longValue();

        // Test
        List<Review> result = reviewDao.findAll(filter, ordering,activeUserId);

        // Assertions
        Assert.assertEquals(result.size(), 200);
        Assert.assertTrue(result.stream().allMatch(review -> review.getFeedback() == ReviewFeedback.LIKE));
    }

    @Test
    public void testDeleteReview() {
        // Setup
        Number reviewId = addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);

        // Test
        boolean result = reviewDao.deleteReview(reviewId.longValue());

        // Assertions
        Assert.assertTrue(result);
        Optional<Review> deletedReview = jdbcTemplate.query("SELECT * FROM reviews WHERE id = ?", CommonRowMappers.REVIEW_ROW_MAPPER, reviewId).stream().findFirst();
        Assert.assertFalse(deletedReview.isPresent());
    }

    @Test
    public void testDeleteReviewNonExistent() {
        // Setup
        Long nonExistentReviewId = 12345L;

        // Test
        boolean result = reviewDao.deleteReview(nonExistentReviewId);

        // Assertions
        Assert.assertFalse(result);
    }

    @Test
    public void testGetReviewFeedback() {
        // Setup
        Number r1 = addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 1, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Number r2 = addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 1, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        likeReview(r1.longValue(), firstUser.longValue(), true);

        // Test
        ReviewFeedback feedback1 = reviewDao.getReviewFeedback(r1.longValue(), firstUser.longValue());
        ReviewFeedback feedback2 = reviewDao.getReviewFeedback(r2.longValue(), firstUser.longValue());

        // Assertions
        Assert.assertEquals(feedback1, ReviewFeedback.LIKE);
        Assert.assertNull(feedback2);

    }

    @Test
    public void testEditReviewFeedback() {
        // Setup
        Number r1 = addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 1, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Number r2 = addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 1, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        likeReview(r1.longValue(), firstUser.longValue(), true);

        // Test
        boolean changed = reviewDao.editReviewFeedback(r1.longValue(), firstUser.longValue(), ReviewFeedback.LIKE, ReviewFeedback.DISLIKE);

        // Assertions
        Assert.assertTrue(changed);
        ReviewFeedback feedback = jdbcTemplate.query("SELECT * FROM reviewfeedback WHERE userid = ? and reviewid = ?",
                        (rs, rowNum) -> ReviewFeedback.valueOf(rs.getString("feedback")),firstUser.longValue(), r1.longValue())
                .stream().findFirst().orElse(null);
        Assert.assertEquals(feedback, ReviewFeedback.DISLIKE);
    }

    @Test
    public void testEditReviewFeedbackNonExistent() {
        // Setup
        Number r1 = addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 1, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);

        // Test
        boolean changed = reviewDao.editReviewFeedback(r1.longValue(), firstUser.longValue(), ReviewFeedback.LIKE, ReviewFeedback.DISLIKE);

        // Assertions
        Assert.assertFalse(changed);
    }

    @Test
    public void testAddReviewFeedback() {
        // Setup
        Number r1 = addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 1, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);

        // Test
        boolean changed = reviewDao.addReviewFeedback(r1.longValue(), firstUser.longValue(), ReviewFeedback.LIKE);

        // Assertions
        Assert.assertTrue(changed);
        ReviewFeedback feedback = jdbcTemplate.query("SELECT * FROM reviewfeedback WHERE userid = ? and reviewid = ?",
                        (rs, rowNum) -> ReviewFeedback.valueOf(rs.getString("feedback")),firstUser.longValue(), r1.longValue())
                .stream().findFirst().orElse(null);
        Assert.assertEquals(feedback, ReviewFeedback.LIKE);
    }

    @Test
    public void testDeleteReviewFeedback() {
        // Setup
        Number r1 = addReviewRow(firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 1, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        likeReview(r1.longValue(), firstUser.longValue(), true);

        // Test
        boolean changed = reviewDao.deleteReviewFeedback(r1.longValue(), firstUser.longValue(), ReviewFeedback.LIKE);

        // Assertions
        Assert.assertTrue(changed);
        ReviewFeedback feedback = jdbcTemplate.query("SELECT * FROM reviewfeedback WHERE userid = ? and reviewid = ?",
                        (rs, rowNum) -> ReviewFeedback.valueOf(rs.getString("feedback")),firstUser.longValue(), r1.longValue())
                .stream().findFirst().orElse(null);
        Assert.assertNull(feedback);
    }

}
