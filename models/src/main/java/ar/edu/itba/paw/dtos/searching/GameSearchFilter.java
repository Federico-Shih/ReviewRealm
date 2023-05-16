package ar.edu.itba.paw.dtos.searching;

import java.util.List;

// To be used between Webapp and Services
public class GameSearchFilter {
    private final String search;
    private final List<Integer> genres;
    private final Float minRating;
    private final Float maxRating;
    private final Boolean isSuggestion;

    public GameSearchFilter(String search, List<Integer> genres, Float minRating, Float maxRating, Boolean isSuggestion) {
        this.search = search;
        this.genres = genres;
        this.minRating = minRating;
        this.maxRating = maxRating;
        this.isSuggestion = isSuggestion;
    }

    public String getSearch() {
        return search;
    }

    public List<Integer> getGenres() {
        return genres;
    }

    public Float getMinRating() {
        return minRating;
    }

    public Float getMaxRating() {
        return maxRating;
    }

    public Boolean getSuggestion() {
        return isSuggestion;
    }
}
