package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.persistenceinterfaces.GenreDao;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreDao genreDao;
    @Autowired
    public GenreServiceImpl(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Genre> getGenreById(Integer id) {
        return genreDao.getById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Genre> getAllGenres() {
        return genreDao.getAll();
    }
}
