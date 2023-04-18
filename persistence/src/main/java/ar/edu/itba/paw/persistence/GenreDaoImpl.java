package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.persistenceinterfaces.GenreDao;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class GenreDaoImpl implements GenreDao {
    @Override
    public Optional<Genre> getById(Integer id) {
        return Genre.getById(id);
    }

    @Override
    public List<Genre> getAll() {
        return Arrays.asList(Genre.values());
    }
}
