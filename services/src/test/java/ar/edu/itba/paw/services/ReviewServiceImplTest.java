package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.enums.FeedbackType;
import ar.edu.itba.paw.exceptions.ReviewNotFoundException;
import ar.edu.itba.paw.exceptions.UserIsAuthorException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
import ar.edu.itba.paw.servicesinterfaces.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static ar.edu.itba.paw.services.utils.GameTestModels.getSuperGameA;
import static ar.edu.itba.paw.services.utils.ReviewTestModels.*;
import static ar.edu.itba.paw.services.utils.ReviewTestModels.getReview1;
import static ar.edu.itba.paw.services.utils.UserTestModels.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class ReviewServiceImplTest {

    @Mock
    private ReviewDao reviewDao;
    @Mock
    private UserService userService;
    @Mock
    private GameService gameService;
    @Mock
    private ReportService reportService;

    @Mock
    private MailingService mailingService;

    @Mock
    private MissionService missionService;

    @InjectMocks
    private ReviewServiceImpl rs;

    @Test
    public void testGetNonexistentReview() {
        Mockito.when(reviewDao.findById(anyLong(),anyLong())).thenReturn(Optional.empty());

        Optional<Review> optReview = rs.getReviewById(getReview1().getId(),getUser1().getId());

        Assert.assertFalse(optReview.isPresent());
    }

    @Test
    public void testCreateReview() {
        Mockito.when(reviewDao.create(any(), any(), anyInt(), any(), any(), any(), anyDouble(), any(), anyBoolean(), anyBoolean())).thenReturn(getReview1());
        Mockito.when(userService.getFollowers(anyLong(), any())).thenReturn(new Paginated<>(1,1,1, 2, Arrays.asList(getUser2(), getUser3())));
        Mockito.when(userService.isNotificationEnabled(eq(getUser2().getId()), any())).thenReturn(true);
        Mockito.when(userService.isNotificationEnabled(eq(getUser3().getId()), any())).thenReturn(false);
        Mockito.when(gameService.getGameById(anyLong(),anyLong())).thenReturn(Optional.of(getSuperGameA()));
        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.of(getUser1()));
        Mockito.when(reviewDao.findAll(any(), any(), any(), any())).thenReturn(new Paginated<>(1,1,1, 0, new ArrayList<>()));

        Review review = rs.createReview(
                getReview1().getTitle(),
                getReview1().getContent(),
                getReview1().getRating(),
                getReview1().getId(),
                getReview1().getReviewedGame().getId(),
                getReview1().getDifficulty(),
                getReview1().getGameLength(),
                getReview1().getPlatform(),
                getReview1().getCompleted(),
                getReview1().getReplayability());

        Assert.assertNotNull(review);
        Assert.assertEquals(getUser1(), review.getAuthor());
        Assert.assertEquals(getSuperGameA(), review.getReviewedGame());
        Assert.assertEquals(getReview1().getId(), review.getId());
    }

    @Test(expected = ReviewNotFoundException.class)
    public void testUpdateReviewDoesntExist() {
        Mockito.when(reviewDao.findById(eq(getReview1().getId()), any())).thenReturn(Optional.empty());

        Review reviewResult = rs.updateReview(
                getReview1().getId(),
                getReview1().getTitle(),
                getReview1().getContent(),
                getReview1().getRating(),
                getReview1().getDifficulty(),
                getReview1().getGameLength(),
                getReview1().getPlatform(),
                getReview1().getCompleted(),
                getReview1().getReplayability());
    }

    @Test
    public void testUpdateReview() {
        Mockito.when(reviewDao.findById(eq(getReview1().getId()), any())).thenReturn(Optional.of(getReview1()));
        Mockito.when(reviewDao.update(eq(getReview1().getId()), any())).thenReturn(Optional.of(getReview1()));

        Review reviewResult = rs.updateReview(
                getReview1().getId(),
                getReview1().getTitle(),
                getReview1().getContent(),
                getReview1().getRating(),
                getReview1().getDifficulty(),
                getReview1().getGameLength(),
                getReview1().getPlatform(),
                getReview1().getCompleted(),
                getReview1().getReplayability());

        Assert.assertEquals(getReview1().getId(), reviewResult.getId());
        Assert.assertEquals(getReview1().getContent(), reviewResult.getContent());
        Assert.assertEquals(getReview1().getRating(), reviewResult.getRating());
        Assert.assertEquals(getReview1().getPlatform(), reviewResult.getPlatform());
        Assert.assertEquals(getReview1().getReplayability(), reviewResult.getReplayability());
    }


    @Test
    public void testGetUserReviews() {
        Mockito.when(reviewDao.findAll(any(), any(), any(), any()))
                .thenReturn(new Paginated<>(1, 1, 1, 0, new ArrayList<>()));

        Paginated<Review> reviews = rs.getUserReviews(Page.with(1, 1), getUser1().getId(), null);
        Assert.assertEquals(1, reviews.getTotalPages());
        Assert.assertEquals(0, reviews.getList().size());
    }

    @Test
    public void deleteReviewTestNotFound() {
        Mockito.when(reviewDao.findById(eq(getReview1().getId()), any())).thenReturn(Optional.empty());
        Assert.assertFalse(rs.deleteReviewById(getReview1().getId(), 1L));
    }

    @Test
    public void deleteReviewSuccessTest() {
        Review r1 = getReview1();
        Mockito.when(reviewDao.findById(eq(r1.getId()), any())).thenReturn(Optional.of(r1));
        Mockito.when(reviewDao.deleteReview(eq(r1.getId()))).thenReturn(true);
        Mockito.when(userService.isNotificationEnabled(eq(r1.getAuthor().getId()), any())).thenReturn(true);
        boolean deleted = rs.deleteReviewById(r1.getId(), 1L);
        Assert.assertTrue(deleted);
    }

    @Test
    public void feedbackUpdateNoChangeTest() {
        Mockito.when(reviewDao.getReviewFeedback(eq(getReview1().getId()), eq(getUser2().getId()))).thenReturn(Optional.empty());
        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.of(getUser2()));
        Mockito.when(reviewDao.findById(anyLong(), nullable(Long.class))).thenReturn(Optional.of(getReview1()));

        ReviewFeedback result = rs.updateOrCreateReviewFeedback(getReview1().getId(), getUser2().getId(), null);
        Assert.assertNull(result);
    }

    @Test
    public void feedbackUpdateRemoveFeedbackTest() {
        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.of(getUser2()));
        Mockito.when(reviewDao.findById(anyLong(), nullable(Long.class))).thenReturn(Optional.of(getReview1()));
        Mockito.when(reviewDao.getReviewFeedback(eq(getReview1().getId()), eq(getUser2().getId()))).thenReturn(Optional.of(FeedbackType.LIKE));
        Mockito.when(reviewDao.editReviewFeedback(eq(getReview1().getId()), eq(getUser2().getId()), eq(FeedbackType.LIKE), eq(FeedbackType.DISLIKE))).thenReturn(Optional.of(new ReviewFeedback(getUser2(), getReview1(), FeedbackType.DISLIKE)));

        ReviewFeedback result = rs.updateOrCreateReviewFeedback(getReview1().getId(), getUser2().getId(), FeedbackType.DISLIKE);
        Assert.assertNotNull(result);
        Assert.assertEquals(FeedbackType.DISLIKE, result.getFeedback());
    }

    @Test
    public void feedbackUpdateNoPreviousFeedback() {
        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.of(getUser2()));
        Mockito.when(reviewDao.findById(anyLong(), nullable(Long.class))).thenReturn(Optional.of(getReview1()));
        Mockito.when(reviewDao.getReviewFeedback(eq(getReview1().getId()), eq(getUser2().getId()))).thenReturn(Optional.empty());
        Mockito.when(reviewDao.addReviewFeedback(eq(getReview1().getId()), eq(getUser2().getId()), eq(FeedbackType.LIKE))).thenReturn(Optional.of(new ReviewFeedback(getUser2(), getReview1(), FeedbackType.LIKE)));

        ReviewFeedback result = rs.updateOrCreateReviewFeedback(getReview1().getId(), getUser2().getId(), FeedbackType.LIKE);
        Assert.assertNotNull(result);
        Assert.assertEquals(FeedbackType.LIKE, result.getFeedback());
    }

    @Test(expected = UserIsAuthorException.class)
    public void feedbackUpdateOwnReview() {
        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.of(getUser1()));
        Mockito.when(reviewDao.findById(anyLong(), nullable(Long.class))).thenReturn(Optional.of(getReview1()));
        rs.updateOrCreateReviewFeedback(getReview1().getId(), getUser1().getId(), FeedbackType.LIKE);
    }

}
