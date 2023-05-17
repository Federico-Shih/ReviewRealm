package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.enums.ReviewFeedback;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.MailingService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class ReviewServiceImplTest {

    private static final long REVIEWID = 23;

    private static final User USER = new User(1L, "InflusearchGames", "email", "password");
    private static final User USER2 = new User(2L, "InflusearchGames", "email", "password");
    private static final User USER3 = new User(3L, "InflusearchGames", "email", "password");
    private static final Game GAME = new Game(3L, "Martians Attack"
            , "", "", "", "", new ArrayList<>(), LocalDate.now(), 8.8);
    private static final Review REVIEW = new Review(23L, USER,
            "", "", LocalDateTime.now(), 8, GAME, Difficulty.EASY,
            12.2, Platform.PS, true, true, null, 0L);
    private static final Review REVIEW2 = new Review(24L, USER2,
            "", "", LocalDateTime.now(), 8, GAME, Difficulty.EASY,
            12.2, Platform.PS, true, true, null, 0L);
    private static final Review REVIEW3 = new Review(25L, USER3, "", "", LocalDateTime.now(), 8, GAME, Difficulty.EASY,
            12.2, Platform.PS, true, true, null, 0L);

    private static final Review REVIEW4 = new Review(26L, USER3, "", "", LocalDateTime.now(), 5, GAME, Difficulty.EASY,
            12.2, Platform.PS, true, true, null, 0L);

    @Mock
    private ReviewDao reviewDao;
    @Mock
    private UserService userService;
    @Mock
    private GameService gameService;

    @Mock
    private MailingService mailingService;

    @InjectMocks
    private ReviewServiceImpl rs;

    @Test
    public void testGetReviewById() {
        Mockito.when(reviewDao.findById(REVIEWID,null))
                .thenReturn(Optional.of(new Review(23L, USER,
                        "","", LocalDateTime.now(),8,GAME, Difficulty.EASY,
                        12.2, Platform.PS,true,true,null,0L)));

        Optional<Review> optReview = rs.getReviewById(REVIEWID,null);

        Assert.assertTrue(optReview.isPresent());
        long id = optReview.get().getId();
        Assert.assertEquals(REVIEWID, id);
    }
    @Test
    public void testGetReviewByIdNotFound() {
        Mockito.when(reviewDao.findById(REVIEWID,null))
                .thenReturn(Optional.empty());

        Optional<Review> optReview = rs.getReviewById(REVIEWID,null);

        Assert.assertFalse(optReview.isPresent());
    }

    @Test
    public void testCreateReview() {
        Mockito.when(reviewDao.create("title", "content", 8, GAME, USER, Difficulty.EASY, 12.2, Platform.PS, true, true))
                .thenReturn(new Review(REVIEWID, USER,
                        "", "", LocalDateTime.now(), 8, GAME, Difficulty.EASY,
                        12.2, Platform.PS, true, true, null, 0L));
        Mockito.when(userService.getFollowers(anyLong())).thenReturn(Arrays.asList(USER2, USER3));
        Mockito.when(userService.isNotificationEnabled(eq(USER2.getId()), any())).thenReturn(true);
        Mockito.when(userService.isNotificationEnabled(eq(USER3.getId()), any())).thenReturn(false);

        Review review = rs.createReview("title", "content", 8, USER, GAME, Difficulty.EASY, 12.2, Platform.PS, true, true);
        Assert.assertNotNull(review);
        Assert.assertEquals(USER, review.getAuthor());
        Assert.assertEquals(GAME, review.getReviewedGame());
        Assert.assertEquals(REVIEWID, (long) review.getId());
    }

    @Test
    public void testUpdateReviewDoesntExist() {
        Mockito.when(reviewDao.findById(eq(REVIEWID), any()))
                .thenReturn(Optional.empty());
        Assert.assertEquals(rs.updateReview(REVIEWID, "title", "content", 8, Difficulty.EASY, 12.2, Platform.PS, true, true), 0);
    }

    @Test
    public void testUpdateReview() {
        Mockito.when(reviewDao.findById(eq(REVIEWID), any())).thenReturn(Optional.of(new Review(REVIEWID, USER,
                "", "", LocalDateTime.now(), 8, GAME, Difficulty.EASY,
                12.2, Platform.PS, true, true, null, 0L)));
        Mockito.when(reviewDao.update(eq(REVIEWID), any())).thenReturn(1);
        int res = rs.updateReview(REVIEWID, "title", "content", 10, Difficulty.EASY, 12.2, Platform.PS, true, true);
        Assert.assertEquals(res, 1);
    }

    @Test
    public void testUpdateReviewWasFavorite() {
        Mockito.when(reviewDao.findById(eq(REVIEWID), any())).thenReturn(Optional.of(new Review(REVIEWID, USER,
                "", "", LocalDateTime.now(), 8, GAME, Difficulty.EASY,
                12.2, Platform.PS, true, true, null, 0L)));

        Mockito.when(reviewDao.update(eq(REVIEWID), any())).thenReturn(1);
        int res = rs.updateReview(REVIEWID, "title", "content", 5, Difficulty.EASY, 12.2, Platform.PS, true, true);
        Assert.assertEquals(res, 1);
    }

    @Test
    public void testGetUserReviews() {
        Mockito.when(reviewDao.findAll(any(), any(), any(), any()))
                .thenReturn(new Paginated<>(1, 1, 1, new ArrayList<>()));

        Paginated<Review> reviews = rs.getUserReviews(Page.with(1, 1), 1L, null);
        Assert.assertEquals(reviews.getTotalPages(), 1);
        Assert.assertEquals(reviews.getList().size(), 0);
    }

    @Test
    public void testGetFollowingReviews() {
        Mockito.when(reviewDao.findAll(any(), any(), any(), any()))
                .thenReturn(new Paginated<>(1, 1, 1, new ArrayList<>()));
        Mockito.when(userService.getFollowing(anyLong())).thenReturn(Arrays.asList(USER2, USER3));
        Mockito.when(reviewDao.findAll(any(), any(), any(), any()))
                .thenReturn(new Paginated<>(1, 1, 1, Arrays.asList(REVIEW, REVIEW2, REVIEW3)));
        List<Review> reviews = rs.getReviewsFromFollowingByUser(1L, 10);
        Assert.assertTrue(reviews.contains(REVIEW));
        Assert.assertTrue(reviews.contains(REVIEW2));
        Assert.assertTrue(reviews.contains(REVIEW3));
    }

    @Test
    public void deleteReviewTestNotFound() {
        Mockito.when(reviewDao.findById(eq(REVIEWID), any())).thenReturn(Optional.empty());
        Assert.assertFalse(rs.deleteReviewById(REVIEWID));
    }

    @Test
    public void deleteReviewSuccessTest() {
        Mockito.when(reviewDao.findById(eq(REVIEWID), any())).thenReturn(Optional.of(REVIEW));
        Mockito.when(reviewDao.deleteReview(eq(REVIEWID))).thenReturn(true);
        Mockito.when(userService.isNotificationEnabled(eq(REVIEW.getAuthor().getId()), any())).thenReturn(true);
        boolean deleted = rs.deleteReviewById(REVIEWID);
        Assert.assertTrue(deleted);
    }

    @Test
    public void deleteReviewSuccessTestWithoutFavoriteGameDeletion() {
        Mockito.when(reviewDao.findById(eq(REVIEW4.getId()), any())).thenReturn(Optional.of(REVIEW4));
        Mockito.when(reviewDao.deleteReview(eq(REVIEW4.getId()))).thenReturn(true);
        Mockito.when(userService.isNotificationEnabled(eq(REVIEW4.getAuthor().getId()), any())).thenReturn(true);
        boolean deleted = rs.deleteReviewById(REVIEW4.getId());
        Assert.assertTrue(deleted);
    }

    @Test
    public void feedbackUpdateNoChangeTest() {
        Mockito.when(reviewDao.getReviewFeedback(eq(REVIEWID), eq(USER.getId()))).thenReturn(null);
        boolean result = rs.updateOrCreateReviewFeedback(REVIEW, USER, null);
        Assert.assertFalse(result);
    }

    @Test
    public void feedbackUpdateRemoveFeedbackTest() {
        Mockito.when(reviewDao.getReviewFeedback(eq(REVIEWID), eq(USER.getId()))).thenReturn(ReviewFeedback.LIKE);
        Mockito.when(reviewDao.deleteReviewFeedback(eq(REVIEWID), eq(USER.getId()), eq(ReviewFeedback.LIKE))).thenReturn(true);
        boolean result = rs.updateOrCreateReviewFeedback(REVIEW, USER, ReviewFeedback.LIKE);
        Assert.assertTrue(result);
    }

    @Test
    public void feedbackUpdateNoPreviousFeedback() {
        Mockito.when(reviewDao.getReviewFeedback(eq(REVIEWID), eq(USER.getId()))).thenReturn(null);
        Mockito.when(reviewDao.addReviewFeedback(eq(REVIEWID), eq(USER.getId()), eq(ReviewFeedback.LIKE))).thenReturn(true);
        boolean result = rs.updateOrCreateReviewFeedback(REVIEW, USER, ReviewFeedback.LIKE);
        Assert.assertTrue(result);
    }

    @Test
    public void feedbackUpdateChangeFeedbackTest() {
        Mockito.when(reviewDao.getReviewFeedback(eq(REVIEWID), eq(USER.getId()))).thenReturn(ReviewFeedback.DISLIKE);
        Mockito.when(reviewDao.editReviewFeedback(eq(REVIEWID), eq(USER.getId()), eq(ReviewFeedback.DISLIKE), eq(ReviewFeedback.LIKE))).thenReturn(true);
        boolean result = rs.updateOrCreateReviewFeedback(REVIEW, USER, ReviewFeedback.LIKE);
        Assert.assertTrue(result);
    }
}
