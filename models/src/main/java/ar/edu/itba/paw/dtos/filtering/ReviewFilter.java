package ar.edu.itba.paw.dtos.filtering;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import java.util.List;

// To be used between Services and Persistence
public class ReviewFilter {
    private final List<Integer> filterGameGenres;
    private final List<Integer> authorPreferences;
    private final List<Long> authors;
    private final String reviewContent;
    private final Long gameId;
    private final Double minTimePlayed;
    private final List<Platform> platforms;
    private final List<Difficulty> difficulties;
    private final Boolean completed;

    private final Boolean replayable;

    private final Boolean orBetweenGenres;

    private final List<Long> authorsToExclude;

    private final List<Long> gamesToExclude;

    private final Boolean deleted;

    public ReviewFilter(List<Integer> filterGameGenres, List<Integer> authorPreferences, List<Long> authors, String reviewContent, Long gameId, Double minTimePlayed, List<Platform> platforms, List<Difficulty> difficulties, Boolean completed, Boolean replayable,Boolean orBetweenGenres, List<Long> authorsToExclude, List<Long> gamesToExclude, Boolean deleted) {
        this.filterGameGenres = filterGameGenres;
        this.authorPreferences = authorPreferences;
        this.authors = authors;
        this.reviewContent = reviewContent;
        this.gameId = gameId;
        this.minTimePlayed = minTimePlayed;
        this.platforms = platforms;
        this.difficulties = difficulties;
        this.completed = completed;
        this.replayable = replayable;
        this.orBetweenGenres = orBetweenGenres;
        this.authorsToExclude = authorsToExclude;
        this.gamesToExclude = gamesToExclude;
        this.deleted = deleted;
    }

    public List<Integer> getFilterGameGenres() {
        return filterGameGenres;
    }

    public Long getGameId() {
        return gameId;
    }

    public List<Integer> getAuthorPreferences() {
        return authorPreferences;
    }

    public List<Long> getAuthors() {
        return authors;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public Double getMinTimePlayed() {
        return minTimePlayed;
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public List<Difficulty> getDifficulties() {
        return difficulties;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public Boolean getReplayable() {
        return replayable;
    }

    public Boolean getOrBetweenGenres() { return orBetweenGenres; }

    public List<Long> getAuthorsToExclude() { return authorsToExclude; }

    public List<Long> getGamesToExclude() { return gamesToExclude; }

    public Boolean getDeleted() {
        return deleted;
    }
}
