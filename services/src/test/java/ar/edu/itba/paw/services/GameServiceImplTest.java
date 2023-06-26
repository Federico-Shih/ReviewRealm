package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.saving.SubmitGameDTO;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.exceptions.GameNotFoundException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import ar.edu.itba.paw.servicesinterfaces.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.*;

import static ar.edu.itba.paw.services.utils.GameTestModels.getSuperGameA;
import static ar.edu.itba.paw.services.utils.GameTestModels.getSuperGameB;
import static ar.edu.itba.paw.services.utils.ReviewTestModels.*;
import static ar.edu.itba.paw.services.utils.UserTestModels.getUser1;
import static org.mockito.ArgumentMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceImplTest {

    @Mock
    private GameDao gameDao;

    @Mock
    private ReviewService reviewService;

    @Mock
    private UserService userService;

    @Mock
    private ImageService imgService;
    @Mock
    private MailingService mailingService;
    @Mock
    private MissionService missionService;
    @Mock
    private User user;
    @InjectMocks
    private GameServiceImpl gs;

    @Mock
    private SubmitGameDTO dto;

    @Test
    public void testGetGameById() {
        Mockito.when(gameDao.getById(getSuperGameA().getId()))
                .thenReturn(Optional.of(getSuperGameA()));

        Optional<Game> opt = gs.getGameById(getSuperGameA().getId());

        Assert.assertTrue(opt.isPresent());
        Long id = opt.get().getId();
        Assert.assertEquals(getSuperGameA().getId(), id);
        Assert.assertEquals(getSuperGameA().getName(), opt.get().getName());
    }

    @Test
    public void testCantFindGameById() {
        Mockito.when(gameDao.getById(getSuperGameA().getId()))
                .thenReturn(Optional.empty());
        Optional<Game> opt = gs.getGameById(getSuperGameA().getId());

        Assert.assertFalse(opt.isPresent());

    }

    @Test
    public void testGameReviewDataByGameId(){
        Mockito.when(gameDao.getById(anyLong())).thenReturn(Optional.of(getSuperGameA()));
        Mockito.when(reviewService.getAllReviewsFromGame(getSuperGameA().getId(),null)).thenReturn(Arrays.asList(getReview1(),getReview2(),getReview3()));

        GameReviewData data = gs.getGameReviewDataByGameId(getSuperGameA().getId());

        Assert.assertEquals(Difficulty.EASY, data.getAverageDifficulty());
        Assert.assertEquals(Platform.PS, data.getAveragePlatform());
        Assert.assertEquals((getReview1().getGameLength() + getReview2().getGameLength() + getReview3().getGameLength()) / 3.0, data.getAverageGameTime(), 0.001);
        double expectedReplayability = 100.0* ((getReview1().getReplayability() ? 1 : 0) + (getReview2().getReplayability() ? 1 : 0) + (getReview3().getReplayability() ? 1 : 0)) / 3;
        Assert.assertEquals(expectedReplayability,data.getReplayability(), 0.001);
        double expectedCompletability = 100.0* ((getReview1().getCompleted() ? 1 : 0) + (getReview2().getCompleted() ? 1 : 0) + (getReview3().getCompleted() ? 1 : 0)) / 3;
        Assert.assertEquals(expectedCompletability,data.getCompletability(), 0.001);
    }

    @Test
    public void testGameReviewDataByGameIdNoReviews(){
        Mockito.when(gameDao.getById(anyLong())).thenReturn(Optional.of(getSuperGameA()));
        Mockito.when(reviewService.getAllReviewsFromGame(getSuperGameA().getId(),null)).thenReturn(new ArrayList<>());

        GameReviewData data = gs.getGameReviewDataByGameId(getSuperGameA().getId());

        Assert.assertNull(data.getAverageDifficulty());
        Assert.assertNull(data.getAveragePlatform());
        Assert.assertEquals(-1, data.getAverageGameTime(), 0.001);
        Assert.assertEquals(-1 ,data.getReplayability(), 0.001);
        Assert.assertEquals(-1 ,data.getCompletability(), 0.001);
    }

    @Test(expected = GameNotFoundException.class)
    public void testGameReviewDataByGameIdNoGame(){
        Mockito.when(gameDao.getById(anyLong())).thenReturn(Optional.empty());

        GameReviewData data = gs.getGameReviewDataByGameId(getSuperGameA().getId());
    }

    @Test(expected = GameNotFoundException.class)
    public void testAcceptGameError(){
        Mockito.when(gameDao.setSuggestedFalse(anyLong())).thenReturn(Optional.empty());

        gs.acceptGame(getSuperGameA().getId(), getUser1().getId());
    }



    @Test(expected = GameNotFoundException.class)
    public void testRejectGameError(){
        Mockito.when(gameDao.getById(anyLong())).thenReturn(Optional.empty());

        gs.rejectGame(getSuperGameA().getId(), getUser1().getId());
    }

    @Test
    public void testGetRecommendationsOfGamesForUser() {
        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(user.getPreferences()).thenReturn(new HashSet<>(Arrays.asList(Genre.ACTION, Genre.ADVENTURE)));
        Mockito.when(user.hasPreferencesSet()).thenReturn(true);
        Mockito.when(gameDao.getGamesReviewedByUser(anyLong())).thenReturn(Optional.of(new HashSet<>(Arrays.asList(getSuperGameA()))));
        Mockito.when(gameDao.getRecommendationsForUser(any(), any())).thenReturn(Arrays.asList(getSuperGameB()));

        List<Game> games = gs.getRecommendationsOfGamesForUser(getUser1().getId());

        Assert.assertEquals(1, games.size());
        Assert.assertEquals(getSuperGameB().getId(), games.get(0).getId());
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetRecommendationsOfGamesForUserError() {
        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.empty());
        gs.getRecommendationsOfGamesForUser(getUser1().getId());
    }

    @Test
    public void testCreateGame(){
        Mockito.when(imgService.uploadImage(any(),any())).thenReturn(new Image("a","jpg",new byte[0]));
        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(user.getRoles()).thenReturn(new HashSet<>(Collections.singletonList(RoleType.MODERATOR)));
        Mockito.when(dto.getGenres()).thenReturn(Arrays.asList(Genre.ACTION.getId(), Genre.ADVENTURE.getId()));

        Mockito.when(gameDao.create(any(),any(),any(),any(),any(),any(),any(),eq(false),any())).thenReturn(getSuperGameA());

        Optional<Game> game = gs.createGame(dto,getUser1().getId());

        Assert.assertTrue(game.isPresent());
        Assert.assertEquals(getSuperGameA().getId(),game.get().getId());
    }

    @Test
    public void testCreateGameNonModerator(){
        Mockito.when(imgService.uploadImage(any(),any())).thenReturn(new Image("a","jpg",new byte[0]));
        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(user.getRoles()).thenReturn(new HashSet<>());
        Mockito.when(dto.getGenres()).thenReturn(Arrays.asList(Genre.ACTION.getId(), Genre.ADVENTURE.getId()));

        Mockito.when(gameDao.create(any(),any(),any(),any(),any(),any(),any(),eq(true),any())).thenReturn(getSuperGameA());

        Optional<Game> game = gs.createGame(dto,getUser1().getId());

        Assert.assertFalse(game.isPresent());
    }

    @Test(expected = UserNotFoundException.class)
    public void testCreateGameError(){
        Mockito.when(imgService.uploadImage(any(),any())).thenReturn(new Image("a","jpg",new byte[0]));
        Mockito.when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

        gs.createGame(dto,getUser1().getId());
    }

    @Test
    public void testEditGame(){
        Mockito.when(dto.getGenres()).thenReturn(Arrays.asList(Genre.ACTION.getId(), Genre.ADVENTURE.getId()));
        Mockito.when(dto.getImageData()).thenReturn(new byte[0]);

        Mockito.when(gameDao.edit(anyLong(),any(),any(),any(),any(),any(),any(),any())).thenReturn(Optional.of(getSuperGameA()));

        Game game = gs.editGame(dto,getUser1().getId());

        Assert.assertEquals(getSuperGameA().getId(),game.getId());
    }

}
