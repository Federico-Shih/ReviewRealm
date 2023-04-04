package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Genre;
import ar.edu.itba.paw.persistenceinterfaces.GenreDao;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
@Repository
public class GenreDaoImpl implements GenreDao {

    @Override
    public Genre create(String name) {
        return new Genre(1,"FPS");
    }

    @Override
    public Genre getById(Integer id) {
        return new Genre(1,"FPS");
    }

    @Override
    public List<Genre> getAll() {
        return Arrays.asList(getById(0), getById(0));
    }
}
