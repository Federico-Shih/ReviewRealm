package ar.edu.itba.paw.dtos.filtering;


import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;

import java.util.List;

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

    public ReviewFilter(List<Integer> filterGameGenres, List<Integer> authorPreferences, List<Long> authors, String reviewContent, Long gameId, Double minTimePlayed, List<Platform> platforms, List<Difficulty> difficulties, Boolean completed) {
        this.filterGameGenres = filterGameGenres;
        this.authorPreferences = authorPreferences;
        this.authors = authors;
        this.reviewContent = reviewContent;
        this.gameId = gameId;
        this.minTimePlayed = minTimePlayed;
        this.platforms = platforms;
        this.difficulties = difficulties;
        this.completed = completed;
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
}
