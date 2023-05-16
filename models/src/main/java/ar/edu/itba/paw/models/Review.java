package ar.edu.itba.paw.models;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.GamelengthUnit;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.enums.ReviewFeedback;
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
    private final Difficulty difficulty;
    private final Double gameLength;
    private final Platform platform;
    private final Boolean completed;
    private final Boolean replayability;
    private ReviewFeedback feedback;

    private final Long likeCounter;

    public Review(Long id, User author, String title, String content, LocalDateTime created, Integer rating, Game reviewedGame, Difficulty difficulty, Double gameLength, Platform platform, Boolean completed, Boolean replayability, ReviewFeedback feedback, Long likeCounter) {
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
        this.feedback = feedback;
        this.likeCounter = likeCounter;
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

    public void setFeedback(ReviewFeedback feedback) {
        if(this.feedback == null) {
            this.feedback = feedback;
        }
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

    public Pair<GamelengthUnit, Double> getGameLengthInUnits() {
        GamelengthUnit unit;
        double gametime;
        if(gameLength == null) {
            return null;
        }
        if (gameLength > GamelengthUnit.HOURS.toSeconds(1.0)) {
            gametime = gameLength / GamelengthUnit.HOURS.toSeconds(1.0);
            unit = GamelengthUnit.HOURS;
        } else {
            gametime = gameLength / GamelengthUnit.MINUTES.toSeconds(1.0);
            unit = GamelengthUnit.MINUTES;
        }
        return Pair.of(unit, gametime);
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

    public ReviewFeedback getFeedback() {
        return feedback;
    }

    public Long getLikeCounter() {
        return likeCounter;
    }
}
