package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.ReportDao;
import ar.edu.itba.paw.services.utils.UserTestModels;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.access.method.P;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static ar.edu.itba.paw.services.utils.ReportTestModels.getReport1;
import static ar.edu.itba.paw.services.utils.UserTestModels.*;
import static org.mockito.ArgumentMatchers.any;
import static ar.edu.itba.paw.services.utils.ReviewTestModels.*;
import static ar.edu.itba.paw.services.utils.ReportTestModels.*;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceImplTest {

    @Mock
    private ReportDao reportDao;
    @Mock
    private ReviewService reviewService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    public void testCreateReport(){
        Mockito.when(userService.getUserById(eq(getUser2().getId()))).thenReturn(Optional.of(getUser2()));
        Mockito.when(reviewService.getReviewById(eq(getReview1().getId()), any())).thenReturn(Optional.of(getReview1()));
        Mockito.when(reportDao.create(eq(getUser2().getId()),eq(getReview1().getId()),eq(ReportReason.SPAM))).thenReturn(getReport1());
        Mockito.when(reportDao.findAll(any(), any())).thenReturn(new Paginated<>(1,10,0, Collections.emptyList()));

        Report report = reportService.createReport(getUser2().getId(),getReview1().getId(),ReportReason.SPAM);

        Assert.assertEquals(getReport1().getReportedReview().getId(),report.getReportedReview().getId());
        Assert.assertEquals(getReport1().getReporter().getId(),report.getReporter().getId());
        Assert.assertEquals(getReport1().getReason(),report.getReason());
    }

    @Test(expected = UserNotFoundException.class)
    public void testCreateReportUserNotExists() throws UserNotFoundException {
        Mockito.when(userService.getUserById(eq(getUser5().getId()))).thenReturn(Optional.empty());

        reportService.createReport(getUser5().getId(),getReview1().getId(),ReportReason.SPAM);

    }
    @Test(expected = ReviewNotFoundException.class)
    public void testCreateReportReviewNotExists() throws ReviewNotFoundException {
        Mockito.when(userService.getUserById(eq(getUser2().getId()))).thenReturn(Optional.of(getUser2()));
        Mockito.when(reviewService.getReviewById(eq(getReview2().getId()), any())).thenReturn(Optional.empty());

        reportService.createReport(getUser2().getId(),getReview2().getId(),ReportReason.DISRESPECTFUL);

    }
    @Test(expected = ReportAlreadyExistsException.class)
    public void testCreateReportAlreadyExists() throws ReportAlreadyExistsException{
        Mockito.when(userService.getUserById(eq(getUser2().getId()))).thenReturn(Optional.of(getUser2()));
        Mockito.when(reviewService.getReviewById(eq(getReview1().getId()), any())).thenReturn(Optional.of(getReview1()));
        Mockito.when(reportDao.findAll(any(), any())).thenReturn(new Paginated<Report>(1,10,1, Collections.singletonList(getReport1())));

        reportService.createReport(getUser2().getId(),getReview1().getId(),ReportReason.SPAM);
    }

    @Test
    public void testResolveReport(){
        Mockito.when(reportDao.get(eq(getReport1().getId()))).thenReturn(Optional.of(getReport1()));
        Mockito.when(userService.getUserById(eq(getUser4().getId()))).thenReturn(Optional.of(getUser4()));
        Mockito.when(reportDao.updateStatus(eq(getReport1().getId()),eq(getUser4().getId()),eq(true))).thenReturn(getReport1());

        Report report = reportService.resolveReport(getReport1().getId(),getUser4().getId());

        Assert.assertEquals(getReport1().getReason(),report.getReason());
        Assert.assertEquals(getReport1().getReportedReview().getAuthor().getId(),report.getReportedUser().getId());
    }

    @Test(expected = UserNotAModeratorException.class)
    public void testResolveNotModerator() throws UserNotAModeratorException{
        Mockito.when(reportDao.get(eq(getReport1().getId()))).thenReturn(Optional.of(getReport1()));
        Mockito.when(userService.getUserById(eq(getUser1().getId()))).thenReturn(Optional.of(getUser1()));
        reportService.resolveReport(getReport1().getId(),getUser1().getId());
    }
    @Test(expected = ReportNotFoundException.class)
    public void testResolveReportNotExists() throws ReportNotFoundException{
        Mockito.when(reportDao.get(eq(getReport1().getId()))).thenReturn(Optional.empty());

        reportService.resolveReport(getReport1().getId(),getUser1().getId());
    }
    @Test(expected = UserNotFoundException.class)
    public void testResolveReportModeratorNotExists() throws UserNotFoundException{
        Mockito.when(reportDao.get(eq(getReport1().getId()))).thenReturn(Optional.of(getReport1()));
        Mockito.when(userService.getUserById(eq(getUser4().getId()))).thenReturn(Optional.empty());

        Report report = reportService.resolveReport(getReport1().getId(),getUser4().getId());

        Assert.assertNull(report);
    }
    @Test()
    public void testDeleteReportNotFound(){
        Mockito.when(reportDao.delete(getReport1().getId())).thenReturn(false);
        Assert.assertFalse(reportService.deleteReport(getReport1().getId()));
    }




}
