package ar.edu.itba.paw.persistence.tests;


import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.ReportFilter;
import ar.edu.itba.paw.dtos.filtering.ReportFilterBuilder;
import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.persistence.helpers.CommonRowMappers;
import ar.edu.itba.paw.persistence.tests.utils.ReportTestModels;
import ar.edu.itba.paw.persistence.tests.utils.UserTestModels;
import ar.edu.itba.paw.persistenceinterfaces.ReportDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.List;
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


    private static final Long NON_EXISTENT_REVIEW = 100L;
    private static final  Long NON_EXISTENT_USER = 100L;

    private static final Long NON_EXISTENT_REPORT = 100L;

    private Report testReport;
    private Report testReport2;
    private Report testCreateReport;

    private static final Page DEFAULT_PAGE = Page.with(1, 10);
    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        testReport = ReportTestModels.getReport1();
        testReport2 = ReportTestModels.getReport2();
        testCreateReport = ReportTestModels.getCreateReport();
    }


    @Rollback
    @Test
    public void testCreateSuccessful() {

        Report answer = reportDao.create(testCreateReport.getReporter().getId(), testCreateReport.getReportedReview().getId(), testCreateReport.getReason());

        Assert.assertNotNull(answer);
        em.flush();

        Report toCompare = jdbcTemplate.queryForObject("Select * from reports where id = ?", CommonRowMappers.REPORT_ROW_MAPPER,answer.getId());

        Assert.assertEquals(answer.getId(), toCompare.getId());

        Assert.assertEquals(testCreateReport.getReason().toString(), toCompare.getReason().toString());

    }
    @Rollback
    @Test
    public void testCreateUnsuccessful(){

        Report answer = reportDao.create(NON_EXISTENT_USER, testCreateReport.getReportedReview().getId(), testCreateReport.getReason());

        Assert.assertNull(answer);

        answer = reportDao.create(testCreateReport.getReporter().getId(), NON_EXISTENT_REVIEW, testCreateReport.getReason());;

        Assert.assertNull(answer);

        answer = reportDao.create(testCreateReport.getReporter().getId(), testCreateReport.getReportedReview().getId(), null);

        Assert.assertNull(answer);
    }
    @Rollback
    @Test
    public void testDeleteSuccessful(){

        boolean ans = reportDao.delete(testReport.getId());
        em.flush();

        Assert.assertTrue(ans);
        List<Report> toCompare = jdbcTemplate.query("Select * from reports where id = ?", CommonRowMappers.REPORT_ROW_MAPPER,testReport.getId());
        Assert.assertEquals(0, toCompare.size());
    }
    @Rollback
    @Test
    public void testDeleteUnsuccessful(){

        boolean ans = reportDao.delete(NON_EXISTENT_REPORT);

        Assert.assertFalse(ans);
        Assert.assertNotNull(jdbcTemplate.queryForObject("Select * from reports where id = ?", CommonRowMappers.REPORT_ROW_MAPPER, testReport.getId()));
    }
    @Rollback
    @Test
    public void testGetSuccessful(){

        Optional<Report> answer = reportDao.get(testReport.getId());

        Assert.assertTrue(answer.isPresent());
        Assert.assertEquals(testReport.getReporter().getId(), answer.get().getReporter().getId());
        Assert.assertEquals(testReport.getReportedReview().getId(), answer.get().getReportedReview().getId());
    }
    @Rollback
    @Test
    public void testGetUnsuccessful(){
        Optional<Report> answer = reportDao.get(NON_EXISTENT_REPORT);

        Assert.assertFalse(answer.isPresent());
    }
    @Rollback
    @Test
    public void testFindAllWithReporterId(){

        ReportFilter filter = new ReportFilterBuilder().withReporterId(ReportTestModels.getCommonReporter().getId()).build();
        List<Report> answer = reportDao.findAll(DEFAULT_PAGE, filter).getList();

        Assert.assertEquals(2, answer.size());
        Assert.assertEquals(ReportTestModels.getCommonReporter().getId(), answer.get(0).getReporter().getId());
        Assert.assertEquals(ReportTestModels.getCommonReporter().getId(), answer.get(1).getReporter().getId());

    }
    @Rollback
    @Test
    public void testFindAllWithReviewId(){
        Review review = ReportTestModels.getCommonReportedReview();

        ReportFilter filter = new ReportFilterBuilder().withReviewId(review.getId()).build();
        List<Report> answer = reportDao.findAll(DEFAULT_PAGE, filter).getList();

        Assert.assertEquals(2, answer.size());
        Assert.assertEquals(review.getId(), answer.get(0).getReportedReview().getId());
        Assert.assertEquals(review.getId(), answer.get(1).getReportedReview().getId());
    }
    @Rollback
    @Test
    public void testFindAllWithReportedUser(){

        User user = ReportTestModels.getCommonReportedReview().getAuthor();
        ReportFilter filter = new ReportFilterBuilder().withReportedUserId(user.getId()).build();

        List<Report> answer = reportDao.findAll(DEFAULT_PAGE, filter).getList();

        Assert.assertEquals(2, answer.size());
        Assert.assertEquals(user.getId(), answer.get(0).getReportedReview().getId());
        Assert.assertEquals(user.getId(), answer.get(1).getReportedUser().getId());
    }
    @Rollback
    @Test
    public void testUpdateStatusResolve(){

        Report ans = reportDao.updateStatus(testReport.getId(), UserTestModels.getUser4().getId(),true);

        Assert.assertNotNull(ans);
        Assert.assertNull(ans.getReportedReview());
        Assert.assertTrue(ans.isResolved());
        Assert.assertEquals(UserTestModels.getUser4().getId(), ans.getModerator().getId());
    }
    @Rollback
    @Test
    public void testUpdateStatusReject(){

        Report ans = reportDao.updateStatus(testReport.getId(), UserTestModels.getUser4().getId(),false);

        Assert.assertNotNull(ans);
        Assert.assertNotNull(ans.getReportedReview());
        Assert.assertTrue(ans.isResolved());
        Assert.assertEquals(UserTestModels.getUser4().getId(), ans.getModerator().getId());
    }

}
