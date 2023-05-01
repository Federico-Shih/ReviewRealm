package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.persistenceinterfaces.GenreDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class GenreServiceImplTest {


    private static final Integer GENREID = Genre.ARCADE.getId();

    @Mock
    private GenreDao genreDao;
    @InjectMocks
    private GenreServiceImpl gs;

    @Test
    public void testGetGenreById() {
        Mockito.when(genreDao.getById(GENREID)).thenReturn(Optional.of(Genre.ARCADE));

        Optional<Genre> gen = gs.getGenreById(GENREID);

        Assert.assertTrue(gen.isPresent());
        Assert.assertEquals(gen.get(), Genre.ARCADE);
    }
}
