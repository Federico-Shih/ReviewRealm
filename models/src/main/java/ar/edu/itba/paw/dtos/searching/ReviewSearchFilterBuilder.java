package ar.edu.itba.paw.dtos.searching;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import java.util.List;

// To be used between Webapp and Services
public class ReviewSearchFilterBuilder {
    private List<Integer> genres = null;
    private List<Integer> preferences = null;
    private Double minTimePlayed = null;
    private List<Platform> platforms = null;
    private List<Difficulty> difficulties = null;
    private Boolean completed = null;
    private Boolean replayable = null;
    private String search = null;

    public ReviewSearchFilterBuilder withGenres(List<Integer> genres) {
        this.genres = genres;
        return this;
    }

    public ReviewSearchFilterBuilder withPreferences(List<Integer> preferences) {
        this.preferences = preferences;
        return this;
    }

    public ReviewSearchFilterBuilder withMinTimePlayed(Double minTimePlayed) {
        this.minTimePlayed = minTimePlayed;
        return this;
    }

    public ReviewSearchFilterBuilder withPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
        return this;
    }

    public ReviewSearchFilterBuilder withDifficulties(List<Difficulty> difficulties) {
        this.difficulties = difficulties;
        return this;
    }

    public ReviewSearchFilterBuilder withCompleted(Boolean completed) {
        this.completed = completed;
        return this;
    }

    public ReviewSearchFilterBuilder withReplayable(Boolean replayable) {
        this.replayable = replayable;
        return this;
    }

    public ReviewSearchFilterBuilder withSearch(String search) {
        this.search = search;
        return this;
    }

    public ReviewSearchFilter build() {
        return new ReviewSearchFilter(genres, preferences, minTimePlayed, platforms, difficulties, completed, replayable, search);
    }

}
