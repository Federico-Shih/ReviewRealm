package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.models.Genre;

import java.util.List;

public interface GenreService {
    Genre createGenre(String name);
    Genre getGenreById(Integer id);
    List<Genre> getAllGenres();

}
