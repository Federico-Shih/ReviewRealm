package ar.edu.itba.paw.dtos.searching;

import java.util.List;

public class GameSearchFilterBuilder {

    private String search = null;
    private List<Integer> genres = null;
    private Float minRating = null;
    private Float maxRating = null;
    private Boolean isSuggestion = null;

    public GameSearchFilterBuilder withSearch(String search) {
        this.search = search;
        return this;
    }

    public GameSearchFilterBuilder withGenres(List<Integer> genres) {
        this.genres = genres;
        return this;
    }

    public GameSearchFilterBuilder withRatingRange(Float minRating, Float maxRating) {
        this.minRating = minRating;
        this.maxRating = maxRating;
        return this;
    }

    public GameSearchFilterBuilder withSuggestion(Boolean isSuggestion) {
        this.isSuggestion = isSuggestion;
        return this;
    }

    public GameSearchFilter build() {
        return new GameSearchFilter(search, genres, minRating, maxRating, isSuggestion);
    }
}
