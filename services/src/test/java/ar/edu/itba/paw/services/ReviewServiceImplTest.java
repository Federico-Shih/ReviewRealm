package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.GamelengthUnit;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
import ar.edu.itba.paw.servicesinterfaces.GameService;
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
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ReviewServiceImplTest {

    private static final long REVIEWID = 23;

    private static final User USER = new User(1L, "InflusearchGames", "email", "password", new ArrayList<>(), true);

    private static final Game GAME = new Game(3L,"Martians Attack"
            ,"","","","",new ArrayList<>(), LocalDate.now(),8.8);
    @Mock
    private ReviewDao reviewDao;
    @Mock
    private UserService userService;
    @Mock
    private GameService gameService;

    @InjectMocks
    private ReviewServiceImpl rs;

    @Test
    public void testGetReviewById() {
        Mockito.when(reviewDao.getById(REVIEWID))
                .thenReturn(Optional.of(new Review(23L, USER,
                        "","", LocalDateTime.now(),8,GAME, Difficulty.EASY,
                        12.2, Platform.PS,true,true)));

        Optional<Review> optReview = rs.getReviewById(REVIEWID);

        Assert.assertTrue(optReview.isPresent());
        long id = optReview.get().getId();
        Assert.assertEquals(REVIEWID, id);
    }
}
