package ar.edu.itba.paw.dtos.searching;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import java.util.List;

// To be used between Webapp and Services
public class ReviewSearchFilter {
    private final List<Integer> genres;
    private final List<Integer> preferences;
    private final Double minTimePlayed;
    private final List<Platform> platforms;
    private final List<Difficulty> difficulties;
    private final Boolean completed;
    private final String search;

    public ReviewSearchFilter(List<Integer> genres, List<Integer> preferences, Double minTimePlayed, List<Platform> platforms, List<Difficulty> difficulties, Boolean completed, String search) {
        this.genres = genres;
        this.preferences = preferences;
        this.minTimePlayed = minTimePlayed;
        this.platforms = platforms;
        this.difficulties = difficulties;
        this.completed = completed;
        this.search = search;
    }

    public List<Integer> getGenres() {
        return genres;
    }

    public List<Integer> getPreferences() {
        return preferences;
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

    public String getSearch() {
        return search;
    }
}
