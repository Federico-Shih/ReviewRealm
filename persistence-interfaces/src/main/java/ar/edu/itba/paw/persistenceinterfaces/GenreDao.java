package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.models.Genre;

import java.util.List;

public interface GenreDao {
    Genre create(String name);
    Genre getById(Integer id);
    List<Genre> getAll();
}
