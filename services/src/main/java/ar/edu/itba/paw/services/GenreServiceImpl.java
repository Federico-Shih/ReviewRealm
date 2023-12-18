package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GenreServiceImpl implements GenreService {
    @Override
    public Optional<Genre> getGenreById(int id) {
        return Genre.getById(id);
    }

    @Override
    public List<Genre> getGenres() {
        return Arrays.stream(Genre.values()).collect(Collectors.toList());
    }
}
