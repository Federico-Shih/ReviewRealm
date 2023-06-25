package ar.edu.itba.paw.persistence.tests;


import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.ReportFilter;
import ar.edu.itba.paw.dtos.filtering.ReportFilterBuilder;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.keys.ReportId;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistenceinterfaces.ReportDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ReportDaoTest{

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataSource ds;

    @Autowired
    private ReportDao reportDao;

    private JdbcTemplate jdbcTemplate;

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

    private static final Long REVIEW_ID1 = 1L;

    private static final Long REVIEW_ID2 = 2L;
    //Game fields
    private static final Long FIRST_GAME = 1L;
    private static final Long SECOND_GAME = 2L;

    private static final String DESCRIPTION = "game description";
    private static final String DEVELOPER = "game developer";
    private static final String PUBLISHER = "game publisher";
    private static final Boolean SUGGESTED = false;

    private static final String NAME = "game name";
    //User fields
    private static final Long FIRST_USER = 1L;
    private static final Long SECOND_USER = 2L;

    private static final Long THIRD_USER = 3L;
    private static final String USERNAME1 = "username1";
    private static final String USERNAME2 = "username2";
    private static final String USERNAME3 = "username3";

    private static final String PASSWORD = "password";
    private static final String EMAIL1 = "email";
    private static final String EMAIL2 = "email2";

    private static final String EMAIL3 = "email3";
    private static final boolean ENABLED = true;
    //Images
    private static final String IMAGE_ID = "id1";
    private static final byte[] IMAGE = "E".getBytes();
    private static final String MEDIA_TYPE = "image/jpeg";

    private static final Long UNEXISTENT_REVIEW = 100L;
    private static final  Long UNEXISTENT_USER = 100L;

    private static final Long UNEXISTENT_REPORT = 100L;

    private static final LocalDateTime PREV_DATE = LocalDateTime.of(2019, 1, 1, 0, 0);

    private static final LocalDateTime BETWEEN_DATE = LocalDateTime.of(2019, 12, 1, 0, 0);

    private static final LocalDateTime LATER_DATE = LocalDateTime.of(2020, 1, 1, 0, 0);

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "reports");
    }
    @Before
    public void globalsetUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "games");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "images");

        SimpleJdbcInsert jdbcInsertForUsers = new SimpleJdbcInsert(ds).withTableName("users");
        SimpleJdbcInsert jdbcInsertForGames = new SimpleJdbcInsert(ds).withTableName("games");
        SimpleJdbcInsert jdbcInsertForImages = new SimpleJdbcInsert(ds).withTableName("images");
        SimpleJdbcInsert jdbcInsertForReviews = new SimpleJdbcInsert(ds).withTableName("reviews");
        Map<String, Object> args = new HashMap<>();

        //Setup users
        args.put("username", USERNAME1);
        args.put("password", PASSWORD);
        args.put("email", EMAIL1);
        args.put("enabled", ENABLED);
        args.put("reputation", 0L);
        args.put("id", FIRST_USER);
        jdbcInsertForUsers.execute(args);
        args.put("username", USERNAME2);
        args.put("email", EMAIL2);
        args.put("id", SECOND_USER);
        jdbcInsertForUsers.execute(args);
        args.put("username", USERNAME3);
        args.put("email", EMAIL3);
        args.put("id", THIRD_USER);
        jdbcInsertForUsers.execute(args);
        //Setup images
        args.clear();
        args.put("data", IMAGE);
        args.put("id", IMAGE_ID);
        args.put("mediaType", MEDIA_TYPE);
        jdbcInsertForImages.execute(args);
        //Setup games
        args.clear();
        args.put("id", FIRST_GAME);
        args.put("name", NAME);
        args.put("description", DESCRIPTION);
        args.put("developer", DEVELOPER);
        args.put("publisher", PUBLISHER);
        args.put("suggestion", SUGGESTED);
        args.put("publishDate", LocalDate.now());
        args.put("image", IMAGE_ID);
        jdbcInsertForGames.execute(args);
        args.put("id", SECOND_GAME);
        jdbcInsertForGames.execute(args);
        //Setup reviews
        args.clear();
        args.put("authorid", FIRST_USER);
        args.put("gameid", FIRST_GAME);
        args.put("title", TITLE);
        args.put("content", CONTENT);
        args.put("rating", RATING);
        args.put("difficulty", DIFFICULTY);
        args.put("platform", PLATFORM);
        args.put("gamelength", GAME_LENGTH);
        args.put("createddate", TIMESTAMP);
        args.put("replayability", REPLAYABILITY);
        args.put("completed", COMPLETED);
        args.put("likes", LIKES);
        args.put("dislikes", DISLIKES);
        args.put("id", REVIEW_ID1);
        jdbcInsertForReviews.execute(args);
        args.put("id", REVIEW_ID2);
        jdbcInsertForReviews.execute(args);
    }
    private Report reportSetup() {
        User reporter = em.find(User.class, SECOND_USER);
        Review review = em.find(Review.class, REVIEW_ID1);
        Report report = new Report(reporter, review,ReportReason.SPAM, LocalDateTime.now());
        em.persist(report);
        em.flush();
        return report;
    }

    @Test
    public void testCreateSuccessful() {
        User reporter = em.find(User.class, SECOND_USER);
        Review review = em.find(Review.class, REVIEW_ID1);

        Report answer = reportDao.create(SECOND_USER, REVIEW_ID1, ReportReason.SPAM);

        Assert.assertNotNull(answer);
        em.flush();

        Report toCompare = em.find(Report.class, answer.getId());

        Assert.assertEquals(reporter.getId(), toCompare.getReporter().getId());

        Assert.assertEquals(review.getId(), toCompare.getReportedReview().getId());

        Assert.assertEquals(ReportReason.SPAM.toString(), toCompare.getReason().toString());

    }
    @Test
    public void testCreateUnsuccessful(){

        Report answer = reportDao.create(UNEXISTENT_USER, REVIEW_ID1, ReportReason.SPAM);

        Assert.assertNull(answer);

        answer = reportDao.create(FIRST_USER,UNEXISTENT_REVIEW, ReportReason.SPAM);

        Assert.assertNull(answer);

        answer = reportDao.create(FIRST_USER, REVIEW_ID1, null);

        Assert.assertNull(answer);
    }
    @Test
    public void testDeleteSuccessful(){
        Report report = reportSetup();

        boolean ans = reportDao.delete(report.getId());

        Assert.assertTrue(ans);
        Assert.assertNull(em.find(Report.class, report.getId()));
    }
    @Test
    public void testDeleteUnsuccessful(){
        Report report = reportSetup();

        boolean ans = reportDao.delete(UNEXISTENT_REPORT);

        Assert.assertFalse(ans);
        Assert.assertNotNull(em.find(Report.class, report.getId()));
    }
    @Test
    public void testGetSuccessful(){
        Report report = reportSetup();

        Optional<Report> answer = reportDao.get(report.getId());

        Assert.assertTrue(answer.isPresent());
        Assert.assertEquals(SECOND_USER, answer.get().getReporter().getId());
        Assert.assertEquals(REVIEW_ID1, answer.get().getReportedReview().getId());
    }

    @Test
    public void testGetUnsuccessful(){
        Report report = reportSetup();

        Optional<Report> answer = reportDao.get(UNEXISTENT_REPORT);

        Assert.assertFalse(answer.isPresent());
    }

    @Test
    public void testFindAllWithReporterId(){
        User reporter = em.find(User.class, SECOND_USER);
        Review review1 = em.find(Review.class, REVIEW_ID1);
        Review review2 = em.find(Review.class, REVIEW_ID2);

        em.persist(new Report(reporter, review1, ReportReason.SPAM, LocalDateTime.now()));
        em.persist(new Report(reporter, review2, ReportReason.SPAM, LocalDateTime.now()));
        em.flush();

        ReportFilter filter = new ReportFilterBuilder().withReporterId(SECOND_USER).build();
        List<Report> answer = reportDao.findAll(Page.with(1,10), filter).getList();

        Assert.assertEquals(2, answer.size());
        Assert.assertEquals(reporter.getId(), answer.get(0).getReporter().getId());
        Assert.assertEquals(reporter.getId(), answer.get(1).getReporter().getId());

    }
    @Test
    public void testFindAllWithReviewId(){
        User reporter1 = em.find(User.class, SECOND_USER);
        User reporter2 = em.find(User.class, FIRST_USER);
        Review review = em.find(Review.class, REVIEW_ID1);

        em.persist(new Report(reporter1, review, ReportReason.SPAM, LocalDateTime.now()));
        em.persist(new Report(reporter2, review, ReportReason.SPAM, LocalDateTime.now()));
        em.flush();

        ReportFilter filter = new ReportFilterBuilder().withReviewId(REVIEW_ID1).build();
        List<Report> answer = reportDao.findAll(Page.with(1,10), filter).getList();

        Assert.assertEquals(2, answer.size());
        Assert.assertEquals(review.getId(), answer.get(0).getReportedReview().getId());
        Assert.assertEquals(review.getId(), answer.get(1).getReportedReview().getId());
    }
    @Test
    public void testFindAllWithReportedUser(){
        Review review = em.find(Review.class, REVIEW_ID1);

        User user2 = em.find(User.class, SECOND_USER);
        User user3 = em.find(User.class, THIRD_USER);

        em.persist(new Report(user2, review, ReportReason.SPAM, LocalDateTime.now()));
        em.persist(new Report(user3, review, ReportReason.SPAM, LocalDateTime.now()));
        em.flush();
        ReportFilter filter = new ReportFilterBuilder().withReportedUserId(FIRST_USER).build();

        List<Report> answer = reportDao.findAll(Page.with(1,10), filter).getList();

        Assert.assertEquals(2, answer.size());
        Assert.assertEquals(review.getId(), answer.get(0).getReportedReview().getId());
        Assert.assertEquals(FIRST_USER, answer.get(1).getReportedUser().getId());
    }
    @Test
    public void testUpdateStatusResolve(){
        Report report = reportSetup();

        Report ans = reportDao.updateStatus(report.getId(),THIRD_USER,true);

        Assert.assertNotNull(ans);
        Assert.assertNull(ans.getReportedReview());
        Assert.assertTrue(ans.isResolved());
        Assert.assertEquals(THIRD_USER, ans.getModerator().getId());
    }
    @Test
    public void testUpdateStatusReject(){
        Report report = reportSetup();

        Report ans = reportDao.updateStatus(report.getId(),THIRD_USER,false);

        Assert.assertNotNull(ans);
        Assert.assertNotNull(ans.getReportedReview());
        Assert.assertTrue(ans.isResolved());
        Assert.assertEquals(THIRD_USER, ans.getModerator().getId());
    }

}
