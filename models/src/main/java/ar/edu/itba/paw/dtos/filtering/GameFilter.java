package ar.edu.itba.paw.dtos.filtering;

import java.util.List;

// To be used between Services and Persistence
public class GameFilter {
    private final List<Integer> gameGenres;
    private final String gameContent;
    private final String developer;
    private final String publisher;
    private final Float minRating;
    private final Float maxRating;
    private final boolean includeNoRating;
    private final Boolean isSuggested;

    public GameFilter(List<Integer> gameGenres, String gameContent, String developer, String publisher, Float minRating, Float maxRating, boolean includeNoRating, Boolean isSuggested) {
        this.gameGenres = gameGenres;
        this.gameContent = gameContent;
        this.developer = developer;
        this.publisher = publisher;
        this.minRating = minRating;
        this.maxRating = maxRating;
        this.includeNoRating = includeNoRating;
        this.isSuggested = isSuggested;
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

    public Float getMinRating() {
        return minRating;
    }

    public Float getMaxRating() {
        return maxRating;
    }

    public boolean getIncludeNoRating() { return includeNoRating; }

    public Boolean getSuggested() { return isSuggested; }
}
