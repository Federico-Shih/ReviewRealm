package ar.edu.itba.paw.dtos.builders;

import ar.edu.itba.paw.dtos.ReviewFilter;

import java.util.List;

public class ReviewFilterBuilder {
    private List<Integer> gameGenres = null;
    private List<Integer> authorGenres = null;
    private List<Long> authors = null;
    private String reviewContent = null;
    private Integer gameId = null;

    public ReviewFilterBuilder withGameGenres(List<Integer> genres) {
        this.gameGenres = genres;
        return this;
    }

    public ReviewFilterBuilder withAuthorGenres(List<Integer> genres) {
        this.authorGenres = genres;
        return this;
    }

    public ReviewFilterBuilder withAuthors(List<Long> authors) {
        this.authors = authors;
        return this;
    }

    public ReviewFilterBuilder withReviewContent(String text) {
        this.reviewContent = text;
        return this;
    }

    public ReviewFilterBuilder withGameId(Integer gameId) {
        this.gameId = gameId;
        return this;
    }

    public ReviewFilter build() {
        return new ReviewFilter(gameGenres, authorGenres, authors, reviewContent, gameId);
    }
}