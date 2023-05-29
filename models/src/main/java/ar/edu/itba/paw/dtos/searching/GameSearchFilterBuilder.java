package ar.edu.itba.paw.dtos.searching;

import java.util.List;

// To be used between Webapp and Services
public class GameSearchFilterBuilder {

    private String search = null;
    private List<Integer> genres = null;
    private Float minRating = null;
    private Float maxRating = null;
    private boolean includeNoRating = true;
    private Boolean isSuggestion = null;

    public GameSearchFilterBuilder withSearch(String search) {
        this.search = search;
        return this;
    }

    public GameSearchFilterBuilder withGenres(List<Integer> genres) {
        this.genres = genres;
        return this;
    }

    public GameSearchFilterBuilder withRatingRange(Float minRating, Float maxRating, boolean includeNoRating) {
        this.minRating = minRating;
        this.maxRating = maxRating;
        this.includeNoRating = includeNoRating;
        return this;
    }

    public GameSearchFilterBuilder withSuggestion(Boolean isSuggestion) {
        this.isSuggestion = isSuggestion;
        return this;
    }

    public GameSearchFilter build() {
        return new GameSearchFilter(search, genres, minRating, maxRating, includeNoRating, isSuggestion);
    }
}
