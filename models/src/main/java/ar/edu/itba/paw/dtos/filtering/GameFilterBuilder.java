package ar.edu.itba.paw.dtos.filtering;

import java.util.List;

// To be used between Services and Persistence
public class GameFilterBuilder {
    private List<Integer> gameGenres = null;
    private String gameContent = null;
    private String developer = null;
    private String publisher = null;
    private Float minRating = null;
    private Float maxRating = null;
    private Boolean isSuggested = null;

    public GameFilterBuilder withGameGenres(List<Integer> genres) {
        this.gameGenres = genres;
        return this;
    }

    public GameFilterBuilder withGameContent(String content) {
        this.gameContent = content;
        return this;
    }

    public GameFilterBuilder withDeveloper(String developer) {
        this.developer = developer;
        return this;
    }

    public GameFilterBuilder withPublisher(String publisher) {
        this.publisher = publisher;
        return this;
    }

    public GameFilterBuilder withRatingRange(Float minRating, Float maxRating) {
        this.minRating = minRating;
        this.maxRating = maxRating;
        return this;
    }

    public GameFilterBuilder withSuggestion(Boolean isSuggested) {
        this.isSuggested = isSuggested;
        return this;
    }

    public GameFilter build() {
        return new GameFilter(gameGenres, gameContent, developer, publisher, minRating, maxRating, isSuggested);
    }
}