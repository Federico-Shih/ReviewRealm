package ar.edu.itba.paw.persistence.tests;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.SaveReviewDTO;
import ar.edu.itba.paw.dtos.filtering.ReviewFilter;
import ar.edu.itba.paw.dtos.filtering.ReviewFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.FeedbackType;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.ReviewHibernateDao;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.helpers.CommonRowMappers;
import ar.edu.itba.paw.persistence.tests.utils.ReviewTestModels;
import ar.edu.itba.paw.persistence.tests.utils.UserTestModels;
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
public class ReviewDaoImplTest {

    private static final String NAME = "game name";

    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReviewHibernateDao reviewDao;

    @PersistenceContext
    private EntityManager em;

    private User testUserReviewed;//2
    private User testUserNotReviewed;//1
    private Review testReview;//2
    private Review createReview;

    @Before
    public void setUp(){
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.testUserReviewed = UserTestModels.getUser2(); //Review1
        this.testUserNotReviewed = UserTestModels.getUser1();//Review2
        this.testReview = ReviewTestModels.getReview1();
        this.createReview = ReviewTestModels.getCreateReview();

    }


    private void likeReview(long reviewid, long userid, boolean like) {
        Map<String, Object> args = new HashMap<>();
        args.put("reviewid", reviewid);
        args.put("userid", userid);
        args.put("feedback", like ? "LIKE" : "DISLIKE");
    }

    @Rollback
    @Test
    public void testFindByIDNoReviewFeedback() {
        //Test
        Optional <Review> review = reviewDao.findById(testReview.getId(),testUserReviewed.getId());

        Assert.assertTrue(review.isPresent());
        Assert.assertEquals(review.get().getId(), testReview.getId());
        Assert.assertEquals(review.get().getAuthor().getUsername(), testReview.getAuthor().getUsername());
        Assert.assertEquals(review.get().getGameLength(), testReview.getGameLength());
        Assert.assertNull(review.get().getFeedback());
    }

    @Rollback
    @Test
    public void testFindByIDNoActiveUser() {

        // Test
        Optional<Review> review = reviewDao.findById(testReview.getId(), null);

        Assert.assertTrue(review.isPresent());
        Assert.assertEquals(review.get().getId(), testReview.getId());
        Assert.assertEquals(review.get().getAuthor().getUsername(), testReview.getAuthor().getUsername());
        Assert.assertNull(review.get().getFeedback());
    }

    @Rollback
    @Test
    public void testFindByIDNonexistent() {
        // Test
        Optional<Review> review = reviewDao.findById(999L, null);

        Assert.assertFalse(review.isPresent());
    }

    @Rollback
    @Test
    public void testFindByIDWithReviewFeedback() {
        // Setup
        // Test
        Optional<Review> review = reviewDao.findById(testReview.getId(), testUserReviewed.getId());

        Assert.assertTrue(review.isPresent());
        Assert.assertEquals(review.get().getId(), testReview.getId());
        Assert.assertEquals(review.get().getAuthor().getId(), testReview.getAuthor().getId());
        Assert.assertEquals(review.get().getFeedback(), FeedbackType.DISLIKE);
    }

    @Rollback
    @Transactional
    @Test
    public void testUpdateReviewSuccess() {
        // Setup
        SaveReviewDTO updatedReviewDTO = new SaveReviewDTO("Updated Title", "Updated Content", 4, Difficulty.EASY, 60.6, Platform.PC, true, false);

        // Test
        Optional<Review> review = reviewDao.update(testReview.getId(), updatedReviewDTO);
        em.flush();
        Assert.assertTrue(review.isPresent());

        // Verify the updated values
        Optional<Review> updatedReview = jdbcTemplate.queryForObject("Select * from reviews where id = ?",CommonRowMappers.TEST_REVIEW_MAPPER,testReview.getId());
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

//    @Rollback
//    @Transactional
//    @Test
//    public void testUpdateNonExistentReview() {
//        // Setup
//        Long nonExistentReviewId = 999L;
//        SaveReviewDTO updatedReviewDTO = new SaveReviewDTO("Updated Title", "Updated Content", 4, Difficulty.EASY, 60.6, Platform.PC, true, false);
//
//        // Test
//        Review result = reviewDao.update(nonExistentReviewId, updatedReviewDTO);
//
//        Assert.assertNull(result);
//    }

    @Rollback
    @Transactional
    @Test
    public void testCreateReviewSuccess() {
        // Setup
        String title = TITLE;
        String content = CONTENT;
        Integer rating = RATING;
        Game reviewedGame = new Game(firstGame.longValue(), NAME, DESCRIPTION, DEVELOPER, PUBLISHER, LocalDate.now(), 7, 3);
        User author = new User(firstUser.longValue(), USERNAME1, EMAIL1, PASSWORD);
        Difficulty difficulty = DIFFICULTY;
        Double gameLength = GAME_LENGTH;
        Platform platform = PLATFORM;
        Boolean completed = COMPLETED;
        Boolean replayable = REPLAYABILITY;

        // Test
        Review createdReview = reviewDao.create(title, content, rating, reviewedGame, author, difficulty, gameLength, platform, completed, replayable);
        em.flush();
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

        Optional<Review> reviewFromDB = jdbcTemplate.query("SELECT * FROM reviews WHERE reviews.id = ?", CommonRowMappers.TEST_REVIEW_MAPPER, createdReview.getId()).stream().findFirst();
        Assert.assertTrue(reviewFromDB.isPresent());
        Review review = reviewFromDB.get();
        Assert.assertEquals(review.getTitle(), title);
        Assert.assertEquals(review.getContent(), content);
        Assert.assertEquals(review.getRating(), rating);
        Assert.assertEquals(review.getDifficulty(), difficulty);
        Assert.assertEquals(review.getGameLength(), gameLength);
        Assert.assertEquals(review.getPlatform(), platform);
        Assert.assertEquals(review.getCompleted(), completed);
        Assert.assertEquals(review.getReplayability(), replayable);
    }

    @Rollback
    @Transactional
    @Test
    public void testCreateWithNullNullableValues() {
        // Setup
        String title = TITLE;
        String content = CONTENT;
        Integer rating = RATING;
        Game reviewedGame = new Game(firstGame.longValue(), NAME, DESCRIPTION, DEVELOPER, PUBLISHER, LocalDate.now(), 7, 3);
        User author = new User(firstUser.longValue(), USERNAME1, EMAIL1, PASSWORD);
        Difficulty difficulty = null;
        Double gameLength = null;
        Platform platform = null;
        Boolean completed = null;
        Boolean replayable = null;

        // Test
        Review createdReview = reviewDao.create(title, content, rating, reviewedGame, author, difficulty, gameLength, platform, completed, replayable);
        em.flush();
        // Verify the created review
        Assert.assertNotNull(createdReview);
        Assert.assertEquals(createdReview.getTitle(), title);
        Assert.assertEquals(createdReview.getContent(), content);
        Assert.assertEquals(createdReview.getRating(), rating);
        Assert.assertEquals(createdReview.getReviewedGame(), reviewedGame);
        Assert.assertNull(createdReview.getDifficulty());
        Assert.assertNull(createdReview.getGameLength());
        Assert.assertNull(createdReview.getPlatform());
        Assert.assertNull(createdReview.getCompleted());
        Assert.assertNull(createdReview.getReplayability());

        Optional<Review> reviewFromDB = jdbcTemplate.query("SELECT * FROM reviews WHERE reviews.id = ?", CommonRowMappers.TEST_REVIEW_MAPPER, createdReview.getId()).stream().findFirst();
        Assert.assertTrue(reviewFromDB.isPresent());
        Review review = reviewFromDB.get();
        Assert.assertEquals(review.getTitle(), title);
        Assert.assertEquals(review.getContent(), content);
        Assert.assertEquals(review.getRating(), rating);
        Assert.assertNull(review.getDifficulty());
        Assert.assertNull(review.getGameLength());
        Assert.assertNull(review.getPlatform());
        Assert.assertFalse(review.getCompleted());
        Assert.assertFalse(review.getReplayability());
    }

//    @Rollback
//    @Transactional
//    @Test
//    public void testCreateNullTitle() {
//        // Setup
//        String title = null;
//        Game reviewedGame = new Game(firstGame.longValue(), NAME, DESCRIPTION, DEVELOPER, PUBLISHER, "", new ArrayList<>(), LocalDate.now(), 7.3);
//        User author = new User(firstUser.longValue(), USERNAME1, EMAIL1, PASSWORD);
//
//        // Test
//        Assert.assertThrows(DataIntegrityViolationException.class, () -> reviewDao.create(title, CONTENT, RATING, reviewedGame, author, DIFFICULTY, GAME_LENGTH, PLATFORM, COMPLETED, REPLAYABILITY));
//
//    }

    @Rollback
    @Transactional
    @Test
    public void testFindAllReviewsWithTitle() {
        // Setup
        addReviewRow(1L, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(2L, firstUser.longValue(), secondGame.longValue(), "HOLA" + TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(3L, secondUser.longValue(), firstGame.longValue(), "HOLA", CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Page pagination = Page.with(1, 10);
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

    @Rollback
    @Transactional
    @Test
    public void testFindAllReviewsWithContent() {
        // Setup
        addReviewRow(1L, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(2L, firstUser.longValue(), secondGame.longValue(), TITLE, "AA" + CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(3L, secondUser.longValue(), firstGame.longValue(), TITLE, "AA", TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Page pagination = Page.with(1, 10);
        ReviewFilter filter = new ReviewFilterBuilder().withReviewContent(CONTENT).build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_SCORE);

        // Test
        Paginated<Review> result = reviewDao.findAll(pagination, filter, ordering, null);

        // Assertions
        Assert.assertEquals(result.getPage(), 1);
        Assert.assertEquals(result.getPageSize(), 10);
        Assert.assertEquals(result.getTotalPages(), 1);
        Assert.assertEquals(result.getList().size(), 2);

        for (Review review : result.getList()) {
            Assert.assertTrue(review.getContent().contains(CONTENT));
        }
    }

    @Rollback
    @Transactional
    @Test
    public void testFindAllReviewsWithAuthor() {
        // Setup
        addReviewRow(1L, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(2L, firstUser.longValue(), secondGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(3L, secondUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Page pagination = Page.with(1, 10);
        ReviewFilter filter = new ReviewFilterBuilder().withAuthors(Collections.singletonList(firstUser.longValue())).build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_SCORE);
        Long activeUserId = null;

        // Test
        Paginated<Review> result = reviewDao.findAll(pagination, filter, ordering, activeUserId);

        // Assertions
        Assert.assertEquals(1, result.getPage());
        Assert.assertEquals(10, result.getPageSize());
        Assert.assertEquals(1, result.getTotalPages());
        Assert.assertEquals(2, result.getList().size());

        for (Review review : result.getList()) {
            Assert.assertEquals((long) review.getAuthor().getId(), firstUser.longValue());
        }
    }

    @Rollback
    @Transactional
    @Test
    public void testFindAllReviewsWithGame() {
        // Setup
        addReviewRow(1L, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(2L, firstUser.longValue(), secondGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(3L, secondUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Page pagination = Page.with(1, 10);
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

    @Rollback
    @Transactional
    @Test
    public void testFindAllReviewsWithGameGenres() {
        // Setup
        addReviewRow(1L, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(2L, firstUser.longValue(), secondGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(3L, secondUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);

        Page pagination = Page.with(1, 10);
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

    @Rollback
    @Transactional
    @Test
    public void testFindAllReviewsWithUserPreferences() {
        // Setup
        addReviewRow(1L, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(2L, firstUser.longValue(), secondGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(3L, secondUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Page pagination = Page.with(1, 10);
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

    @Rollback
    @Transactional
    @Test
    public void testFindAllReviewsWithDifficulty() {
        // Setup
        addReviewRow(1L, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(2L, firstUser.longValue(), secondGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, Difficulty.EASY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(3L, secondUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(4L, secondUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, null, PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);

        Page pagination = Page.with(1, 10);
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
        Assert.assertEquals(result.getList().size(), 2);
    }

    @Rollback
    @Transactional
    @Test
    public void testFindAllReviewsWithPlatform() {
        // Setup
        addReviewRow(1L, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(2L, firstUser.longValue(), secondGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), Platform.PS.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(3L, secondUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Page pagination = Page.with(1, 10);
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

    @Rollback
    @Transactional
    @Test
    public void testFindAllReviewsOrder() {
        // Setup
        addReviewRow(1L, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 5, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(2L, firstUser.longValue(), secondGame.longValue(), TITLE, CONTENT, TIMESTAMP, 7, DIFFICULTY.toString(), Platform.PS.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        addReviewRow(3L, secondUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 2, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Page pagination = Page.with(1, 10);
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

    @Rollback
    @Transactional
    @Test
    public void testFindAllReviewsFeedback() {
        // Setup
        Number r1 = addReviewRow(1L, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 1, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Number r2 = addReviewRow(2L, firstUser.longValue(), secondGame.longValue(), TITLE, CONTENT, TIMESTAMP, 2, DIFFICULTY.toString(), Platform.PS.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        Number r3 = addReviewRow(3L, secondUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 3, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        likeReview(r1.longValue(), firstUser.longValue(), true);
        likeReview(r2.longValue(), firstUser.longValue(), false);
        likeReview(r3.longValue(), secondUser.longValue(), true);

        Page pagination = Page.with(1, 10);
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

        Assert.assertEquals(result.getList().get(0).getFeedback(), FeedbackType.LIKE);
        Assert.assertEquals(result.getList().get(1).getFeedback(), FeedbackType.DISLIKE);
        Assert.assertNull(result.getList().get(2).getFeedback());

    }

    @Rollback
    @Transactional
    @Test
    public void testFindAllReviewsPagination() {
        // Setup
        for (int i = 0; i < 20; i++) {
            addReviewRow(i + 1, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        }

        Page pagination = Page.with(3, 5);
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
    //TODO: Test excluding games and authors in filters

    @Rollback
    @Transactional
    @Test
    public void testNoPaginatingFindAllReviews() {
        // Setup
        for (int i = 0; i < 200; i++) {
            Number reviewId = addReviewRow(i + 1, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
            likeReview(reviewId.longValue(), firstUser.longValue(), true);
        }

        ReviewFilter filter = new ReviewFilterBuilder().build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.ASCENDING, ReviewOrderCriteria.REVIEW_SCORE);
        Long activeUserId = firstUser.longValue();

        // Test
        List<Review> result = reviewDao.findAll(filter, ordering, activeUserId);

        // Assertions
        Assert.assertEquals(result.size(), 200);
        Assert.assertTrue(result.stream().allMatch(review -> review.getFeedback() == FeedbackType.LIKE));
    }

    @Rollback
    @Transactional
    @Test
    public void testDeleteReview() {
        // Setup
        Number reviewId = addReviewRow(1L, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, RATING, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);

        // Test
        boolean result = reviewDao.deleteReview(reviewId.longValue());
        em.flush();
        // Assertions
        Assert.assertTrue(result);
        Optional<Review> deletedReview = jdbcTemplate.query("SELECT * FROM reviews WHERE id = ?", CommonRowMappers.TEST_REVIEW_MAPPER, reviewId).stream().findFirst();
        Assert.assertFalse(deletedReview.isPresent());
    }

    @Rollback
    @Transactional
    @Test
    public void testDeleteReviewNonExistent() {
        // Setup
        Long nonExistentReviewId = 12345L;

        // Test
        boolean result = reviewDao.deleteReview(nonExistentReviewId);

        // Assertions
        Assert.assertFalse(result);
    }

//    @Rollback
//    @Transactional
//    @Test
//    public void testGetReviewFeedback() {
//        // Setup
//        Number r1 = addReviewRow(1L, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 1, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
//        Number r2 = addReviewRow(2L, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 1, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
//        likeReview(r1.longValue(), firstUser.longValue(), true);
//
//        // Test
//        FeedbackType feedback1 = reviewDao.getReviewFeedback(r1.longValue(), firstUser.longValue());
//        FeedbackType feedback2 = reviewDao.getReviewFeedback(r2.longValue(), firstUser.longValue());
//
//        // Assertions
//        Assert.assertEquals(feedback1, FeedbackType.LIKE);
//        Assert.assertNull(feedback2);
//
//    }

//    @Rollback
//    @Transactional
//    @Test
//    public void testEditReviewFeedback() {
//        // Setup
//        Number r1 = addReviewRow(1L, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 1, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
//        Number r2 = addReviewRow(2L, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 1, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
//        likeReview(r1.longValue(), firstUser.longValue(), true);
//
//        // Test
//        boolean changed = reviewDao.editReviewFeedback(r1.longValue(), firstUser.longValue(), FeedbackType.LIKE, FeedbackType.DISLIKE);
//        em.flush();
//        // Assertions
//        Assert.assertTrue(changed);
//        FeedbackType feedback = jdbcTemplate.query("SELECT * FROM reviewfeedback WHERE userid = ? and reviewid = ?",
//                        (rs, rowNum) -> FeedbackType.valueOf(rs.getString("feedback")), firstUser.longValue(), r1.longValue())
//                .stream().findFirst().orElse(null);
//        Assert.assertEquals(feedback, FeedbackType.DISLIKE);
//    }

//    @Rollback
//    @Transactional
//    @Test
//    public void testEditReviewFeedbackNonExistent() {
//        // Setup
//        Number r1 = addReviewRow(1L, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 1, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
//
//        // Test
//        boolean changed = reviewDao.editReviewFeedback(r1.longValue(), firstUser.longValue(), FeedbackType.LIKE, FeedbackType.DISLIKE);
//
//        // Assertions
//        Assert.assertFalse(changed);
//    }
//
//    @Rollback
//    @Transactional
//    @Test
//    public void testAddReviewFeedback() {
//        // Setup
//        Number r1 = addReviewRow(2L, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 1, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
//
//        // Test
//        boolean changed = reviewDao.addReviewFeedback(r1.longValue(), firstUser.longValue(), FeedbackType.LIKE);
//        em.flush();
//        // Assertions
//        Assert.assertTrue(changed);
//        FeedbackType feedback = jdbcTemplate.query("SELECT * FROM reviewfeedback WHERE userid = ? and reviewid = ?",
//                        (rs, rowNum) -> FeedbackType.valueOf(rs.getString("feedback")), firstUser.longValue(), r1.longValue())
//                .stream().findFirst().orElse(null);
//        Assert.assertEquals(feedback, FeedbackType.LIKE);
//    }

    @Rollback
    @Transactional
    @Test
    public void testDeleteReviewFeedback() {
        // Setup
        Number r1 = addReviewRow(1L, firstUser.longValue(), firstGame.longValue(), TITLE, CONTENT, TIMESTAMP, 1, DIFFICULTY.toString(), PLATFORM.toString(), GAME_LENGTH, REPLAYABILITY, COMPLETED, LIKES, DISLIKES);
        likeReview(r1.longValue(), firstUser.longValue(), true);

        // Test
        boolean changed = reviewDao.deleteReviewFeedback(r1.longValue(), firstUser.longValue(), FeedbackType.LIKE);
        em.flush();
        // Assertions
        Assert.assertTrue(changed);
        FeedbackType feedback = jdbcTemplate.query("SELECT * FROM reviewfeedback WHERE userid = ? and reviewid = ?",
                        (rs, rowNum) -> FeedbackType.valueOf(rs.getString("feedback")), firstUser.longValue(), r1.longValue())
                .stream().findFirst().orElse(null);
        Assert.assertNull(feedback);
    }
}
