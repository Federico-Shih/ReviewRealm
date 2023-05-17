package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.saving.SubmitGameDTO;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.exceptions.NoSuchGameException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import ar.edu.itba.paw.servicesinterfaces.ImageService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
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
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceImplTest {

    private static final long GAMEID = 3;

    @Mock
    private GameDao gameDao;

    @Mock
    private ReviewService reviewService;

    @Mock
    private GenreService genreServ;

    @Mock
    private UserService userService;

    @Mock
    private ImageService imgService;
    @Mock
    private User user;
    @InjectMocks
    private GameServiceImpl gs;

    @Mock
    private SubmitGameDTO dto;

    private static final User USER = new User(1L,"","","");
    private static final Game GAME1 = new Game(3L,"Martians Attack"
            ,"","","","",Arrays.asList(Genre.ACTION, Genre.ADVENTURE), LocalDate.now(),7.66);
    private static final Game GAME2 = new Game(4L,"Martians Attack"
            ,"","","","",Arrays.asList(Genre.ACTION, Genre.ADVENTURE), LocalDate.now(),7.66);
    @Test
    public void testGetGameById() {
        Mockito.when(gameDao.getById(GAMEID))
                .thenReturn(Optional.of(GAME1));

        Optional<Game> opt = gs.getGameById(GAMEID);

        Assert.assertTrue(opt.isPresent());
        long id = opt.get().getId();
        Assert.assertEquals(GAMEID, id);
        Assert.assertEquals("Martians Attack", opt.get().getName());
    }

    @Test
    public void testCantFindGameById() {
        Mockito.when(gameDao.getById(GAMEID))
                .thenReturn(Optional.empty());
        Optional<Game> opt = gs.getGameById(GAMEID);

        Assert.assertFalse(opt.isPresent());

    }
    @Test
    public void testGameReviewDataByGameId(){
        Review review1 = new Review(1L,USER,"","", LocalDateTime.now(),8, GAME1,
                Difficulty.EASY,1.5, Platform.PC,true,false,null,5L);
        Review review2 =  new Review(2L,USER,"","", LocalDateTime.now(),10, GAME1,
                Difficulty.EASY,3.0, Platform.PC,true,false,null,10L);
        Review review3 = new Review(3L,USER,"","", LocalDateTime.now(),5, GAME1,
                Difficulty.MEDIUM,7.5, Platform.XBOX,false,true,null,15L);
        Mockito.when(reviewService.getAllReviewsFromGame(GAMEID,null)).thenReturn(Arrays.asList(review1,review2,review3));

        GameReviewData data = gs.getGameReviewDataByGameId(GAMEID);

        Assert.assertEquals(Difficulty.EASY, data.getAverageDifficulty());
        Assert.assertEquals(Platform.PC, data.getAveragePlatform());
        Assert.assertEquals(0,Double.compare((1.5+3.0+7.5)/3,data.getAverageGameTime()));
        Assert.assertEquals(0,Double.compare(GAME1.getAverageRating(),data.getAverageRating()));
        Assert.assertEquals(0,Double.compare((1/3.0)*100,data.getReplayability()));
        Assert.assertEquals(0,Double.compare((2/3.0)*100,data.getCompletability()));
    }
    @Test(expected = NoSuchGameException.class)
    public void testAcceptGameError(){
        Mockito.when(gameDao.setSuggestedFalse(GAME1.getId())).thenReturn(false);
        gs.acceptGame(GAME1.getId());
    }
    @Test(expected = NoSuchGameException.class)
    public void testRejectGameError(){
        Mockito.when(gameDao.deleteGame(GAME1.getId())).thenReturn(false);
        gs.rejectGame(GAME1.getId());
    }

    @Test
    public void testGetRecommendationsOfGamesForUser(){
        Mockito.when(user.getPreferences()).thenReturn(new HashSet<>(Arrays.asList(Genre.ACTION, Genre.ADVENTURE)));
        Mockito.when(user.hasPreferencesSet()).thenReturn(true);
        Mockito.when(gameDao.getGamesReviewdByUser(any())).thenReturn(new HashSet<>(Arrays.asList(GAME1)));
        Mockito.when(gameDao.getRecommendationsForUser(any(),any())).thenReturn(Arrays.asList(GAME2));

        List<Game> games = gs.getRecommendationsOfGamesForUser(user);

        Assert.assertEquals(1,games.size());
        Assert.assertEquals(GAME2.getId(),games.get(0).getId());
    }
    @Test
    public void testCreateGame(){
        Mockito.when(imgService.uploadImage(any(),any())).thenReturn(new Image("a","jpg",new byte[0]));
        Mockito.when(userService.getUserById(any())).thenReturn(Optional.of(user));
        Mockito.when(user.getRoles()).thenReturn(new HashSet<>(Arrays.asList(new Role("MODERATOR"))));
        Mockito.when(dto.getGenres()).thenReturn(Arrays.asList(Genre.ACTION.getId(), Genre.ADVENTURE.getId()));
        Mockito.when(genreServ.getGenreById(Genre.ACTION.getId())).thenReturn(Optional.of(Genre.ACTION));
        Mockito.when(genreServ.getGenreById(Genre.ADVENTURE.getId())).thenReturn(Optional.of(Genre.ADVENTURE));

        Mockito.when(gameDao.create(any(),any(),any(),any(),any(),any(),any(),eq(false))).thenReturn(Optional.of(GAME1));

        Optional<Game> game = gs.createGame(dto,1L);

        Assert.assertTrue(game.isPresent());
        Assert.assertEquals(GAME1.getId(),game.get().getId());
    }
    @Test
    public void testCreateGameNonModerator(){
        Mockito.when(imgService.uploadImage(any(),any())).thenReturn(new Image("a","jpg",new byte[0]));
        Mockito.when(userService.getUserById(any())).thenReturn(Optional.of(user));
        Mockito.when(user.getRoles()).thenReturn(new HashSet<>(new ArrayList<>()));
        Mockito.when(dto.getGenres()).thenReturn(Arrays.asList(Genre.ACTION.getId(), Genre.ADVENTURE.getId()));
        Mockito.when(genreServ.getGenreById(Genre.ACTION.getId())).thenReturn(Optional.of(Genre.ACTION));
        Mockito.when(genreServ.getGenreById(Genre.ADVENTURE.getId())).thenReturn(Optional.of(Genre.ADVENTURE));

        Mockito.when(gameDao.create(any(),any(),any(),any(),any(),any(),any(),eq(true))).thenReturn(Optional.of(GAME1));

        Optional<Game> game = gs.createGame(dto,1L);

        Assert.assertFalse(game.isPresent());
    }

}
