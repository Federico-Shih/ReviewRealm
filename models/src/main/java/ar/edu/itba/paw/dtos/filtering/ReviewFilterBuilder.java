package ar.edu.itba.paw.dtos.filtering;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import java.util.List;

// To be used between Services and Persistence
public class ReviewFilterBuilder {
    private List<Integer> gameGenres = null;
    private List<Integer> authorGenres = null;
    private List<Long> gamesToExclude = null;
    private List<Long> authorsToExclude = null;
    private List<Long> authors = null;
    private String reviewContent = null;
    private Long gameId = null;
    private Double minTimePlayed = null;
    private List<Platform> platforms = null;
    private List<Difficulty> difficulties = null;
    private Boolean completed = null;
    private Boolean replayable = null;
    private Boolean orBetweenGenres = null;

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

    public ReviewFilterBuilder withGameId(Long gameId) {
        this.gameId = gameId;
        return this;
    }

    public ReviewFilterBuilder withMinTimePlayed(Double minTimePlayed) {
        this.minTimePlayed = minTimePlayed;
        return this;
    }

    public ReviewFilterBuilder withPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
        return this;
    }

    public ReviewFilterBuilder withDifficulties(List<Difficulty> difficulties) {
        this.difficulties = difficulties;
        return this;
    }

    public ReviewFilterBuilder withCompleted(Boolean completed) {
        this.completed = completed;
        return this;
    }

    public ReviewFilterBuilder withReplayable(Boolean replayable) {
        this.replayable = replayable;
        return this;
    }

    public  ReviewFilterBuilder withOrBetweenGenres(Boolean orBetweenGenres) {
        this.orBetweenGenres = orBetweenGenres;
        return this;
    }
    public ReviewFilterBuilder withAuthorsToExclude(List<Long> authorsToExclude) {
        this.authorsToExclude = authorsToExclude;
        return this;
    }
    public ReviewFilterBuilder withGamesToExclude(List<Long> gamesToExclude) {
        this.gamesToExclude = gamesToExclude;
        return this;
    }

    public ReviewFilter build() {
        return new ReviewFilter(gameGenres, authorGenres, authors, reviewContent,
                gameId, minTimePlayed, platforms, difficulties, completed, replayable,
                orBetweenGenres,authorsToExclude,gamesToExclude, false);
    }
}