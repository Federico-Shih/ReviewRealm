package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.models.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDao {
    Genre create(String name);
    Optional<Genre> getById(Integer id);
    List<Genre> getAll();
}
