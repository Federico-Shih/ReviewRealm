package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import ar.edu.itba.paw.servicesinterfaces.ImageService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceImplTest {

    private static final long GAMEID = 3;

    @Mock
    private GameDao gameDao;

    @Mock
    private GenreService genreServ;

    @Mock
    private ImageService imgService;

    @InjectMocks
    private GameServiceImpl gs;

    @Test
    public void testGetGameById() {
        Mockito.when(gameDao.getById(GAMEID))
                .thenReturn(Optional.of(new Game(3L,"Martians Attack"
                        ,"","","","",new ArrayList<>(), LocalDate.now(),8.8)));

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
}
