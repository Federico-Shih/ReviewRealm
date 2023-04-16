package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.enums.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDao {
    Optional<Genre> getById(Integer id);
    List<Genre> getAll();
}
