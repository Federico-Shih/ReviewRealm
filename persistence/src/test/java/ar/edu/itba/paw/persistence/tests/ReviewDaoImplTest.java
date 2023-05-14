package ar.edu.itba.paw.persistence.tests;

import ar.edu.itba.paw.dtos.SaveReviewDTO;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.enums.ReviewFeedback;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.ReviewDaoImpl;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.sql.Blob;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


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
    private static final Long REVIEW_ID = 1L;

    private static final String TITLE = "title";

    private static final String CONTENT = "review text";

    private static final Timestamp TIMESTAMP = new Timestamp(System.currentTimeMillis());

    private static final Integer RATING = 10;

    private static final Difficulty DIFFICULTY = Difficulty.HARD;

    private static final Platform PLATFORM = Platform.PC;

    private static final Double GAME_LENGTH = 100.78;

    private static final Boolean REPLAYABILITY = true;

    private static final Boolean COMPLETED = false;

    private static final ReviewFeedback REVIEW_FEEDBACK = ReviewFeedback.LIKE;

    //Game fields
    private static final Long GAME_ID1 = 1L;

    private static final Long GAME_ID2 = 2L;

    private static final String NAME =  "game name";

    private static final String DESCRIPTION = "game description";

    private static final String DEVELOPER = "game developer";

    private static final String PUBLISHER = "game publisher";

    private static final Boolean SUGGESTED = false;

    //User fields
    private static final Long USER_ID1 = 1L;

    private static final Long USER_ID2 = 2L;

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
        jdbcInsertForUsers.execute(args);
        args.put("username",USERNAME2);
        args.put("email",EMAIL2);
        jdbcInsertForUsers.execute(args);

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

        jdbcInsertForGames.executeAndReturnKey(args);//Id = 1
         jdbcInsertForGames.executeAndReturnKey(args);//Id = 2


        //Setup genres
        args.clear();
        args.put("gameid",GAME_ID1);
        args.put("genreid",GENRE_ID1);
        jdbcInsertForGameGenres.execute(args);
        args.put("genreid",GENRE_ID2);
        jdbcInsertForGameGenres.execute(args);
        args.put("gameid",GAME_ID2);
        jdbcInsertForGameGenres.execute(args);
        args.put("genreid",GENRE_ID3);
        jdbcInsertForGameGenres.execute(args);
        //Setup user preferences
        args.clear();

        args.put("userid",USER_ID1);
        args.put("genreid",GENRE_ID1);
        jdbcInsertForUsersGenres.execute(args);
        args.put("genreid",GENRE_ID2);
        jdbcInsertForUsersGenres.execute(args);
        args.put("userid",USER_ID2);
        jdbcInsertForUsersGenres.execute(args);
        args.put("genreid",GENRE_ID3);
        jdbcInsertForUsersGenres.execute(args);

    }
    @Test
    public void testFindByIDNoReviewFeedback(){
        //Setup
        Map<String,Object> reviewArgs = new HashMap<>();

        reviewArgs.put("authorid",USER_ID1);
        reviewArgs.put("gameid",GAME_ID1);
        reviewArgs.put("title",TITLE);
        reviewArgs.put("content",CONTENT);
        reviewArgs.put("createddate",TIMESTAMP);
        reviewArgs.put("rating",RATING);
        reviewArgs.put("difficulty",DIFFICULTY.toString());
        reviewArgs.put("platform",PLATFORM.toString());
        reviewArgs.put("gamelength",GAME_LENGTH);
        reviewArgs.put("replayability",REPLAYABILITY);
        reviewArgs.put("completed",COMPLETED);
        jdbcInsertForReviews.execute(reviewArgs);
        //Test
       Optional<Review> review = reviewDao.findById(1L,USER_ID1);

        Assert.assertTrue(review.isPresent());
        Assert.assertEquals(review.get().getId(),Long.valueOf(1L));
        Assert.assertEquals(review.get().getAuthor().getUsername(),USERNAME1);
        Assert.assertNull(review.get().getFeedback());

    }

//    @Test
//    public void testReviewUpdate() {
//        //Setup
//        Map<String,Object> reviewArgs = new HashMap<>();
//
//        reviewArgs.put("authorid",USER_ID1);
//        reviewArgs.put("gameid",GAME_ID1);
//        reviewArgs.put("title",TITLE);
//        reviewArgs.put("content",CONTENT);
//        reviewArgs.put("createddate",TIMESTAMP);
//        reviewArgs.put("rating",RATING);
//        reviewArgs.put("difficulty",DIFFICULTY.toString());
//        reviewArgs.put("platform",PLATFORM.toString());
//        reviewArgs.put("gamelength",GAME_LENGTH);
//        reviewArgs.put("replayability",REPLAYABILITY);
//        reviewArgs.put("completed",COMPLETED);
//        Long id = jdbcInsertForReviews.executeAndReturnKey(reviewArgs).longValue();
//        //Test
//
//        reviewDao.update(id, new SaveReviewDTO("New Title", "New Content", 5, Difficulty.HARD, 10.0, Platform.PC, true, false));
//        Optional<Review> updatedReview = jdbcTemplate.query("SELECT * FROM reviews WHERE id = ?", (resultSet, i) -> new Review(
//                resultSet.getLong("id"),
//                null,
//                resultSet.getString("title"),
//                resultSet.getString("content"),
//                resultSet.getTimestamp("createddate").toLocalDateTime(),
//                resultSet.getInt("rating"),
//                null,
//                resultSet.getString("difficulty") != null ? Difficulty.valueOf(resultSet.getString("difficulty").toUpperCase()) : null,
//                resultSet.getDouble("gamelength"),
//                resultSet.getString("platform") != null ? Platform.valueOf(resultSet.getString("platform").toUpperCase()) : null,
//                resultSet.getBoolean("completed"),
//                resultSet.getBoolean("replayability"),
//                null,
//                0L
//        ), id).stream().findFirst();
//
//        Assert.assertTrue(updatedReview.isPresent());
//        Assert.assertEquals(updatedReview.get().getId(), id);
//        Assert.assertEquals(updatedReview.get().getTitle(), "New Title");
//        Assert.assertEquals(updatedReview.get().getContent(), "New Content");
//        Assert.assertEquals(5, (int) updatedReview.get().getRating());
//        Assert.assertEquals(updatedReview.get().getDifficulty(), Difficulty.HARD);
//        Assert.assertEquals(10.0, updatedReview.get().getGameLength() != null ? updatedReview.get().getGameLength() : 0, 0.0);
//        Assert.assertEquals(updatedReview.get().getPlatform(), Platform.PC);
//        Assert.assertTrue(updatedReview.get().getCompleted() != null ? updatedReview.get().getCompleted() : false);
//        Assert.assertFalse(updatedReview.get().getReplayability() != null ? updatedReview.get().getReplayability() : true);
//    }
}
