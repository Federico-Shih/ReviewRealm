package ar.edu.itba.paw.exceptions;

public class GenreNotFoundException extends RuntimeException{
    private final Integer genreId;

    public GenreNotFoundException(Integer genreId) {
        this.genreId = genreId;
    }

    public Integer getGenreId() {
        return genreId;
    }

}
