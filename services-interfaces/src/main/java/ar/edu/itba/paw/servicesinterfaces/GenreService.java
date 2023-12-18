package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.enums.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreService {
    Optional<Genre> getGenreById(int id);
    List<Genre> getGenres();
}
