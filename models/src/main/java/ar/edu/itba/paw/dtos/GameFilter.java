package ar.edu.itba.paw.dtos;

import ar.edu.itba.paw.enums.Genre;

import java.util.List;
import java.util.Optional;

public class GameFilter {
    private final List<Integer> gameGenres;
    private final String gameContent;
    private final String developer;
    private final String publisher;

    private final Boolean isFavorite;

    public GameFilter(List<Integer> gameGenres, String gameContent, String developer, String publisher, Boolean isFavorite) {
        this.gameGenres = gameGenres;
        this.gameContent = gameContent;
        this.developer = developer;
        this.publisher = publisher;
        this.isFavorite = isFavorite;
    }

    public List<Integer> getGameGenres() {
        return gameGenres;
    }

    public String getGameContent() {
        return gameContent;
    }

    public String getDeveloper() {
        return developer;
    }

    public String getPublisher() {
        return publisher;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }
}
