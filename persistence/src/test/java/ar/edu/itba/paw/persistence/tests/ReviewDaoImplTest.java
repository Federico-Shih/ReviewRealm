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
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.ReviewHibernateDao;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.helpers.CommonRowMappers;
import ar.edu.itba.paw.persistence.tests.utils.GameTestModels;
import ar.edu.itba.paw.persistence.tests.utils.ReviewTestModels;
import ar.edu.itba.paw.persistence.tests.utils.UserTestModels;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import java.util.stream.Collectors;


@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ReviewDaoImplTest {

    private static final Long NON_EXISTENT_REVIEW_ID = 999L;
    private static final Page DEFAULT_PAGE = Page.with(1,10);
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
    private Review testCreateReview;
    private Review testReviewNoFeedBack;
    private SaveReviewDTO testUpdateReview;
    private Game testGameMultipleReviews;

    private static final Long NO_ACTIVE_USER = null;

    @Before
    public void setUp(){
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.testUserReviewed = UserTestModels.getUser2(); //Review1
        this.testUserNotReviewed = UserTestModels.getUser1();//Review2
        this.testReview = ReviewTestModels.getReview1();
        this.testCreateReview = ReviewTestModels.getCreateReview();
        this.testUpdateReview = ReviewTestModels.getUpdateReview();
        this.testGameMultipleReviews = GameTestModels.getSuperGameB();
        this.testReviewNoFeedBack = ReviewTestModels.getReview3();
    }

    @Rollback
    @Test
    public void testFindByIDNoReviewFeedback() {
        //Test
        Optional <Review> review = reviewDao.findById(testReview.getId(),testUserNotReviewed.getId());

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
        Optional<Review> review = reviewDao.findById(NON_EXISTENT_REVIEW_ID, null);

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
    @Test
    public void testUpdateReviewSuccess() {

        Optional<Review> review = reviewDao.update(testReview.getId(), testUpdateReview);
        em.flush();
        Assert.assertTrue(review.isPresent());

        // Verify the updated values
        Review updatedReview = jdbcTemplate.queryForObject("Select * from reviews where id = ?",CommonRowMappers.TEST_REVIEW_MAPPER,testReview.getId());
        Assert.assertNotNull(updatedReview);
        Assert.assertEquals(updatedReview.getTitle(), testUpdateReview.getTitle());
        Assert.assertEquals(updatedReview.getContent(), testUpdateReview.getContent());
        Assert.assertEquals(updatedReview.getRating(),testUpdateReview.getRating());
        Assert.assertEquals(updatedReview.getDifficulty(),testUpdateReview.getDifficulty());
        Assert.assertTrue(updatedReview.getCompleted());
        Assert.assertFalse(updatedReview.getReplayability());
        Assert.assertEquals(updatedReview.getPlatform(), testUpdateReview.getPlatform());
        Assert.assertEquals(updatedReview.getGameLength(), testUpdateReview.getGameLength());
    }

    @Rollback
    @Test
    public void testUpdateNonExistentReview() {
        // Setup

        // Test
        Optional<Review> result = reviewDao.update(NON_EXISTENT_REVIEW_ID, testUpdateReview);

        Assert.assertFalse(result.isPresent());
    }

    @Rollback
    @Test
    public void testCreateReviewSuccess() {
        // Setup

        // Test
        Review createdReview = reviewDao.create(testCreateReview.getTitle(),
                testCreateReview.getContent(),
                testCreateReview.getRating(),
                testCreateReview.getReviewedGame(),
                testCreateReview.getAuthor(),
                testCreateReview.getDifficulty(),
                testCreateReview.getGameLength(),
                testCreateReview.getPlatform(),
                testCreateReview.getCompleted(),
                testCreateReview.getReplayability());
        em.flush();
        // Verify the created review
        Assert.assertNotNull(createdReview);
        Assert.assertEquals(createdReview.getTitle(), testCreateReview.getTitle());
        Assert.assertEquals(createdReview.getContent(), testCreateReview.getContent());
        Assert.assertEquals(createdReview.getReviewedGame(), testCreateReview.getReviewedGame());
        Assert.assertEquals(createdReview.getAuthor(), testCreateReview.getAuthor());

        Review reviewFromDB = jdbcTemplate.queryForObject("SELECT * FROM reviews WHERE reviews.id = ?", CommonRowMappers.TEST_REVIEW_MAPPER, createdReview.getId());
        Assert.assertNotNull(reviewFromDB);
        Assert.assertEquals(reviewFromDB.getTitle(), testCreateReview.getTitle());
        Assert.assertEquals(reviewFromDB.getContent(), testCreateReview.getContent());
        Assert.assertEquals(reviewFromDB.getRating(), testCreateReview.getRating());
        Assert.assertEquals(reviewFromDB.getDifficulty(), testCreateReview.getDifficulty());
        Assert.assertEquals(reviewFromDB.getGameLength(), testCreateReview.getGameLength());
        Assert.assertEquals(reviewFromDB.getPlatform(), testCreateReview.getPlatform());
        Assert.assertEquals(reviewFromDB.getCompleted(), testCreateReview.getCompleted());
        Assert.assertEquals(reviewFromDB.getReplayability(), testCreateReview.getReplayability());
    }

    @Rollback
    @Test(expected = DataIntegrityViolationException.class)
    public void testCreateNullTitle() throws DataIntegrityViolationException{

        Review createdReview = reviewDao.create(null,
                testCreateReview.getContent(),
                testCreateReview.getRating(),
                testCreateReview.getReviewedGame(),
                testCreateReview.getAuthor(),
                testCreateReview.getDifficulty(),
                testCreateReview.getGameLength(),
                testCreateReview.getPlatform(),
                testCreateReview.getCompleted(),
                testCreateReview.getReplayability());
    }

    @Rollback
    @Test
    public void testFindAllReviewsWithTitle() {
        // Setup
        ReviewFilter filter = new ReviewFilterBuilder().withReviewContent(ReviewTestModels.getCommonTitle()).build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_SCORE);

        // Test
        Paginated<Review> result = reviewDao.findAll(DEFAULT_PAGE, filter, ordering, NO_ACTIVE_USER);

        // Assertions
        Assert.assertEquals(1,result.getPage());
        Assert.assertEquals(10,result.getPageSize() );
        Assert.assertEquals( 1,result.getTotalPages());
        Assert.assertEquals( 3,result.getList().size());

        for (Review review : result.getList()) {
            Assert.assertTrue(review.getTitle().contains(review.getTitle()));
        }
    }

    @Rollback
    @Test
    public void testFindAllReviewsWithContent() {
        // Setup
        ReviewFilter filter = new ReviewFilterBuilder().withReviewContent(ReviewTestModels.getCommonContent()).build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_SCORE);

        // Test
        Paginated<Review> result = reviewDao.findAll(DEFAULT_PAGE, filter, ordering, null);

        // Assertions
        Assert.assertEquals( 3,result.getList().size());

        for (Review review : result.getList()) {
            Assert.assertTrue(review.getContent().contains(ReviewTestModels.getCommonContent()));
        }
    }

    @Rollback
    @Test
    public void testFindAllReviewsWithAuthor() {
        // Setup
        ReviewFilter filter = new ReviewFilterBuilder().withAuthors(Collections.singletonList(ReviewTestModels.getCommonAuthor().getId())).build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_SCORE);

        // Test
        Paginated<Review> result = reviewDao.findAll(DEFAULT_PAGE, filter, ordering, NO_ACTIVE_USER);

        // Assertions

        Assert.assertEquals(2, result.getList().size());

        for (Review review : result.getList()) {
            Assert.assertEquals( review.getAuthor().getId(), ReviewTestModels.getCommonAuthor().getId());
        }
    }

    @Rollback
    @Test
    public void testFindAllReviewsWithGame() {
        // Setup
        ReviewFilter filter = new ReviewFilterBuilder().withGameId(testGameMultipleReviews.getId()).build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_SCORE);

        // Test
        Paginated<Review> result = reviewDao.findAll(DEFAULT_PAGE, filter, ordering, testUserNotReviewed.getId());

        // Assertions

        Assert.assertEquals(2,result.getList().size());

        for (Review review : result.getList()) {
            Assert.assertEquals( review.getReviewedGame().getId(), testGameMultipleReviews.getId());
        }
    }

    @Rollback
    @Test
    public void testFindAllReviewsWithGameGenres() {
        // Setup

        List<Genre> genreList = GameTestModels.getCommonGenres();
        List<Integer> genreIdList =genreList.stream().map(Genre::getId).collect(Collectors.toList());
        ReviewFilter filter = new ReviewFilterBuilder().withGameGenres(genreIdList).build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_SCORE);

        // Test
        Paginated<Review> result = reviewDao.findAll(DEFAULT_PAGE, filter, ordering, NO_ACTIVE_USER);

        // Assertions

        Assert.assertEquals(2,result.getList().size());
        Integer genreId = genreIdList.get(0);

        for (Review review : result.getList()) {
            Assert.assertTrue(review.getReviewedGame().getGenres().stream().anyMatch(genre -> genre.getId() == genreId));
        }
    }


    @Rollback
    @Test
    public void testFindAllReviewsWithDifficulty() {
        // Setup
        ReviewFilter filter = new ReviewFilterBuilder().withDifficulties(Collections.singletonList(Difficulty.EASY)).build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_SCORE);

        // Test
        Paginated<Review> result = reviewDao.findAll(DEFAULT_PAGE, filter, ordering, NO_ACTIVE_USER);

        // Assertions

        Assert.assertEquals(2,result.getList().size());

        Assert.assertTrue(result.getList().stream().anyMatch(review ->review.getDifficulty() == Difficulty.EASY));
    }

    @Rollback
    @Test
    public void testFindAllReviewsOrder() {
        // Setup

        ReviewFilter filter = new ReviewFilterBuilder().build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_SCORE);

        // Test
        Paginated<Review> result = reviewDao.findAll(DEFAULT_PAGE, filter, ordering, NO_ACTIVE_USER);

        // Assertions

        Assert.assertEquals(3,result.getList().size());

        Assert.assertEquals(Integer.valueOf(10),result.getList().get(0).getRating());
        Assert.assertEquals(Integer.valueOf(7),result.getList().get(1).getRating());
        Assert.assertEquals(Integer.valueOf(5),result.getList().get(2).getRating());

    }

    @Rollback
    @Test
    public void testFindAllReviewsPagination() {
        // Setup

        Page pagination = Page.with(1, 2);
        ReviewFilter filter = new ReviewFilterBuilder().build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.ASCENDING, ReviewOrderCriteria.REVIEW_SCORE);

        // Test
        Paginated<Review> result = reviewDao.findAll(pagination, filter, ordering, NO_ACTIVE_USER);

        // Assertions
        Assert.assertEquals( 1,result.getPage());
        Assert.assertEquals(2,result.getPageSize());
        Assert.assertEquals(2,result.getTotalPages());
        Assert.assertEquals(2,result.getList().size());
    }
    //TODO: Test excluding games and authors in filters
    @Rollback
    @Test
    public void testFindAllExcludeAuthor(){
        List<User> authors= Collections.singletonList(ReviewTestModels.getCommonAuthor());
        List<Long> authorIds = authors.stream().map(User::getId).collect(Collectors.toList());
        ReviewFilter filter = new ReviewFilterBuilder().withAuthorsToExclude(authorIds).build();
        Ordering<ReviewOrderCriteria> ordering = new Ordering<>(OrderDirection.ASCENDING, ReviewOrderCriteria.REVIEW_SCORE);

        Paginated<Review> result = reviewDao.findAll(DEFAULT_PAGE, filter, ordering, NO_ACTIVE_USER);

        Assert.assertEquals(1,result.getList().size());
        Review review = result.getList().get(0);
        Assert.assertNotEquals(authorIds.get(0),review.getAuthor().getId());

    }

    @Rollback
    @Test
    public void testDeleteReview() {
        // Setup

        // Test
        boolean result = reviewDao.deleteReview(testReview.getId());
        em.flush();
        // Assertions
        Assert.assertTrue(result);
        Optional<Review> deletedReview = jdbcTemplate.query("SELECT * FROM reviews WHERE id = ?", CommonRowMappers.TEST_REVIEW_MAPPER, testReview.getId()).stream().findFirst();
        Assert.assertFalse(deletedReview.isPresent());
    }

    @Rollback
    @Test
    public void testDeleteReviewNonExistent() {
        boolean result = reviewDao.deleteReview(NON_EXISTENT_REVIEW_ID);
        // Assertions
        Assert.assertFalse(result);
    }

    @Rollback
    @Transactional
    @Test
    public void testGetReviewFeedback() {
        // Setup

        Optional<FeedbackType> feedback1 = reviewDao.getReviewFeedback(testReview.getId(), testUserReviewed.getId());
        Optional<FeedbackType> feedback2 = reviewDao.getReviewFeedback(testReviewNoFeedBack.getId(), testUserReviewed.getId());

        // Assertions
        Assert.assertTrue(feedback1.isPresent());
        Assert.assertEquals(feedback1.get(), FeedbackType.DISLIKE);
        Assert.assertFalse(feedback2.isPresent());
    }

    @Rollback
    @Test
    public void testEditReviewFeedback() {
        // Setup

        // Test
        Optional<ReviewFeedback> changed = reviewDao.editReviewFeedback(testReview.getId(), testUserReviewed.getId(), FeedbackType.DISLIKE, FeedbackType.LIKE);
        em.flush();
        // Assertions
        Assert.assertTrue(changed.isPresent());
        FeedbackType feedback = jdbcTemplate.query("SELECT * FROM reviewfeedback WHERE userid = ? and reviewid = ?",
                        (rs, rowNum) -> FeedbackType.valueOf(rs.getString("feedback")), testUserReviewed.getId(), testReview.getId())
                .stream().findFirst().orElse(null);
        Assert.assertEquals(feedback, FeedbackType.LIKE);
    }

    @Rollback
    @Test
    public void testEditReviewFeedbackNonExistent() {

        // Test
        Optional<ReviewFeedback> changed = reviewDao.editReviewFeedback(testReview.getId(),testUserNotReviewed.getId(), FeedbackType.LIKE, FeedbackType.DISLIKE);

        // Assertions
        Assert.assertFalse(changed.isPresent());
    }

    @Rollback
    @Test
    public void testAddReviewFeedback() {
        // Test
        Optional<ReviewFeedback> changed = reviewDao.addReviewFeedback(testReview.getId(), testUserNotReviewed.getId(), FeedbackType.LIKE);
        em.flush();
        // Assertions
        Assert.assertTrue(changed.isPresent());
        FeedbackType feedback = jdbcTemplate.query("SELECT * FROM reviewfeedback WHERE userid = ? and reviewid = ?",
                        (rs, rowNum) -> FeedbackType.valueOf(rs.getString("feedback")), testUserNotReviewed.getId(), testReview.getId())
                .stream().findFirst().orElse(null);
        Assert.assertEquals(feedback, FeedbackType.LIKE);
    }

    @Rollback
    @Transactional
    @Test
    public void testDeleteReviewFeedback() {

        // Test
        boolean changed = reviewDao.deleteReviewFeedback(testReview.getId(), testUserReviewed.getId(), FeedbackType.LIKE);
        em.flush();
        // Assertions
        Assert.assertTrue(changed);
        FeedbackType feedback = jdbcTemplate.query("SELECT * FROM reviewfeedback WHERE userid = ? and reviewid = ?",
                        (rs, rowNum) -> FeedbackType.valueOf(rs.getString("feedback")), testUserReviewed.getId(), testReview.getId())
                .stream().findFirst().orElse(null);
        Assert.assertNull(feedback);
    }
}
