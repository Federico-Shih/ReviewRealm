package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.ReportDao;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.hibernate.mapping.Any;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceImplTest {

    @Mock
    private ReportDao reportDao;


    @Mock
    private ReviewService reviewService;

    @Mock
    private UserService userService;

    private static final User mockUser = new User(1L,"username","email","password");

    private static final Review mockReview = new Review(1,mockUser,"title","content", LocalDateTime.now(),10,null, Difficulty.HARD,10.0, Platform.PC,true,false,10,21);

    private static final User mockUserModerator = new User(2L,"username2","email2","password2",new HashSet<>(),true,10L,new HashSet<>(),new HashSet<>(Arrays.asList(RoleType.MODERATOR,RoleType.USER)),1L,new HashSet<>(),new HashSet<>());

    private static final User mockUserNotModerator = new User(2L,"username2","email2","password2",new HashSet<>(),true,10L,new HashSet<>(),new HashSet<>(Arrays.asList(RoleType.USER)),1L,new HashSet<>(),new HashSet<>());
    private static final Report mockReport = new Report(mockUser,mockReview, ReportReason.DISRESPECTFUL,LocalDateTime.now());
    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    public void testCreateReport(){
        Mockito.when(userService.getUserById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        Mockito.when(reviewService.getReviewById(mockReview.getId(),null)).thenReturn(Optional.of(mockReview));
        Mockito.when(reportDao.create(mockUser.getId(),mockReview.getId(),ReportReason.DISRESPECTFUL)).thenReturn(mockReport);

        Report report = reportService.createReport(mockUser.getId(),mockReview.getId(),ReportReason.DISRESPECTFUL);

        Assert.assertEquals(mockReport.getReportedReview().getId(),report.getReportedReview().getId());
        Assert.assertEquals(mockReport.getReporter().getId(),report.getReporter().getId());
        Assert.assertEquals(mockReport.getReason(),report.getReason());
    }
    @Test(expected = UserNotFoundException.class)
    public void testCreateReportUserNotExists() throws UserNotFoundException {
       Mockito.when(userService.getUserById(mockUser.getId())).thenReturn(Optional.empty());
       //Mockito.when(reviewService.getReviewById(mockReview.getId(),null)).thenReturn(Optional.of(mockReview));

       reportService.createReport(mockUser.getId(),mockReview.getId(),ReportReason.DISRESPECTFUL);

    }
    @Test(expected = ReviewNotFoundException.class)
    public void testCreateReportReviewNotExists() throws ReviewNotFoundException {
        Mockito.when(userService.getUserById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        Mockito.when(reviewService.getReviewById(mockReview.getId(),null)).thenReturn(Optional.empty());

        reportService.createReport(mockUser.getId(),mockReview.getId(),ReportReason.DISRESPECTFUL);

    }
    @Test(expected = ReportAlreadyExistsException.class)
    public void testCreateReportAlreadyExists() throws ReportAlreadyExistsException{
        Mockito.when(userService.getUserById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        Mockito.when(reviewService.getReviewById(mockReview.getId(),null)).thenReturn(Optional.of(mockReview));
        Mockito.when(reportDao.create(mockUser.getId(),mockReview.getId(),ReportReason.DISRESPECTFUL)).thenReturn(null);

        reportService.createReport(mockUser.getId(),mockReview.getId(),ReportReason.DISRESPECTFUL);
    }
    @Test
    public void testResolveReport(){
        Mockito.when(reportDao.get(mockReport.getId())).thenReturn(Optional.of(mockReport));
        Mockito.when(userService.getUserById(mockUserModerator.getId())).thenReturn(Optional.of(mockUserModerator));
        Mockito.when(reviewService.deleteReviewById(mockReview.getId())).thenReturn(true);

        Report report = reportService.resolveReport(mockReport.getId(),mockUserModerator.getId());

        Assert.assertEquals(mockUserModerator.getId(),report.getModerator().getId());
        Assert.assertEquals(mockReport.getReason(),report.getReason());
        Assert.assertEquals(mockReport.getReportedReview().getAuthor().getId(),report.getReportedUser().getId());
    }

    @Test(expected = UserNotAModeratorException.class)
    public void testResolveNotModerator() throws UserNotAModeratorException{
        Mockito.when(reportDao.get(mockReport.getId())).thenReturn(Optional.of(mockReport));
        Mockito.when(userService.getUserById(mockUserNotModerator.getId())).thenReturn(Optional.of(mockUserNotModerator));

        reportService.resolveReport(mockReport.getId(),mockUserNotModerator.getId());
    }
    @Test(expected = ReportNotFoundException.class)
    public void testResolveReportNotExists() throws ReportNotFoundException{
        Mockito.when(reportDao.get(mockReport.getId())).thenReturn(Optional.empty());

        Report report = reportService.resolveReport(mockReport.getId(),mockUserModerator.getId());

        Assert.assertNull(report);
    }
    @Test(expected = UserNotFoundException.class)
    public void testResolveReportModeratorNotExists() throws UserNotFoundException{
        Mockito.when(reportDao.get(mockReport.getId())).thenReturn(Optional.of(mockReport));
        Mockito.when(userService.getUserById(mockUserModerator.getId())).thenReturn(Optional.empty());

        Report report = reportService.resolveReport(mockReport.getId(),mockUserModerator.getId());

        Assert.assertNull(report);
    }
    @Test(expected = ReportNotFoundException.class)
    public void testDeleteReportNotFound() throws ReportNotFoundException{
        Mockito.when(reportDao.get(mockReport.getId())).thenReturn(Optional.empty());

        reportService.deleteReport(mockReport.getId());
    }




}
