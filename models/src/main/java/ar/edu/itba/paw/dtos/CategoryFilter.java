package ar.edu.itba.paw.dtos;

import ar.edu.itba.paw.models.Category;
import ar.edu.itba.paw.models.Game;

import java.util.Optional;

public class CategoryFilter {
    private final Optional<Game> filteredGame;
    private final Optional<Category> filteredCategory;

    public CategoryFilter(Game filteredGame, Category filteredCategory) {
        this.filteredGame = Optional.of(filteredGame);
        this.filteredCategory = Optional.of(filteredCategory);
    }

    public Optional<Game> getFilteredGame() {
        return filteredGame;
    }

    public Optional<Category> getFilteredCategory() {
        return filteredCategory;
    }
}
