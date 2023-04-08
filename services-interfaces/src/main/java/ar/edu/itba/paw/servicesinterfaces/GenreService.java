package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.models.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreService {
    Genre createGenre(String name);
    Optional<Genre> getGenreById(Integer id);
    List<Genre> getAllGenres();

}
