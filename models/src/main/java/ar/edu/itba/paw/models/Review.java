package ar.edu.itba.paw.models;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import com.sun.istack.internal.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Review {
    private final Long id;
    private final User author;
    private final String title;
    private final String content;
    private final LocalDateTime created;
    private final Integer rating;
    private final Game reviewedGame;
    @Nullable
    private final Difficulty difficulty;
    @Nullable
    private final Double gameLength;
    @Nullable
    private final Platform platform;
    @Nullable
    private final Boolean completed;
    @Nullable
    private final Boolean replayability;

    public Review(Long id, User author, String title, String content, LocalDateTime created, Integer rating, Game reviewedGame, Difficulty difficulty, Double gameLength, Platform platform, Boolean completed, Boolean replayability) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.content = content;
        this.created = created;
        this.rating = rating;
        this.reviewedGame = reviewedGame;
        this.difficulty = difficulty;
        this.gameLength = gameLength;
        this.platform = platform;
        this.completed = completed;
        this.replayability = replayability;
    }

    public Long getId() {
        return id;
    }

    public User getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public String getCreatedFormatted() {
        return created.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
    }

    public Integer getRating() {
        return rating;
    }

    public Game getReviewedGame() {
        return reviewedGame;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Double getGameLength() {
        return gameLength;
    }

    public Platform getPlatform() {
        return platform;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public Boolean getReplayability() {
        return replayability;
    }
}
