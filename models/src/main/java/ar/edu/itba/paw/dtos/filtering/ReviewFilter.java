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

    private final Long recommendedFor;
    private final Long fromFollowing;
    private final Long newForUser;

    public ReviewFilter(List<Integer> filterGameGenres,
                        List<Integer> authorPreferences,
                        List<Long> authors,
                        String reviewContent,
                        Long gameId,
                        Double minTimePlayed,
                        List<Platform> platforms,
                        List<Difficulty> difficulties,
                        Boolean completed,
                        Boolean replayable,
                        Boolean orBetweenGenres,
                        List<Long> authorsToExclude,
                        List<Long> gamesToExclude,
                        Boolean deleted,
                        Long recommendedFor,
                        Long fromFollowing, Long newForUser) {
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
        this.recommendedFor = recommendedFor;
        this.fromFollowing = fromFollowing;
        this.newForUser = newForUser;
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

    public boolean isExclusive() {
        return (filterGameGenres == null || filterGameGenres.isEmpty()) && (authorPreferences == null || authorPreferences.isEmpty())
                && (authors == null || authors.isEmpty()) && (reviewContent == null || reviewContent.isEmpty()) &&
                (gameId == null) && (minTimePlayed == null) &&
                (platforms == null || platforms.isEmpty()) &&
                (difficulties == null || difficulties.isEmpty()) &&
                (completed == null) && (replayable == null) &&
                (orBetweenGenres == null) &&
                (authorsToExclude == null || authorsToExclude.isEmpty()) &&
                (gamesToExclude == null || gamesToExclude.isEmpty());
    }

    public Long getRecommendedFor() {
        return recommendedFor;
    }

    public Long getFromFollowing() {
        return fromFollowing;
    }

    public Long getNewForUser() {
        return newForUser;
    }
}
