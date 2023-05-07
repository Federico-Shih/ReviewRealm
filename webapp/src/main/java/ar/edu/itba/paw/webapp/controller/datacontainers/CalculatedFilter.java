package ar.edu.itba.paw.webapp.controller.datacontainers;

import ar.edu.itba.paw.enums.Genre;

import java.util.List;
import java.util.stream.Collectors;

public class CalculatedFilter  {
    private final List<Genre> unselectedGenres;
    private final List<Genre> selectedGenres;
    private final List<Genre> unselectedPreferences;
    private final List<Genre> selectedPreferences;

    public CalculatedFilter(
            List<Integer> gameGenresFilter,
            List<Integer> reviewerPreferencesFilter,
            List<Genre> allGenres) {
        this.unselectedGenres = allGenres.stream().filter((g) -> !gameGenresFilter.contains(g.getId())).collect(Collectors.toList());
        this.selectedGenres = allGenres.stream().filter((g) -> gameGenresFilter.contains(g.getId())).collect(Collectors.toList());
        this.unselectedPreferences = allGenres.stream().filter((g) -> !reviewerPreferencesFilter.contains(g.getId())).collect(Collectors.toList());
        this.selectedPreferences = allGenres.stream().filter((g) -> reviewerPreferencesFilter.contains(g.getId())).collect(Collectors.toList());
    }

    public List<Genre> getUnselectedGenres() {
        return unselectedGenres;
    }

    public List<Genre> getSelectedGenres() {
        return selectedGenres;
    }

    public List<Genre> getUnselectedPreferences() {
        return unselectedPreferences;
    }

    public List<Genre> getSelectedPreferences() {
        return selectedPreferences;
    }
}