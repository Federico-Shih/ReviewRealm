package ar.edu.itba.paw.dtos;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;

public class SaveReviewDTO {
    private final String title;
    private final String content;
    private final Integer rating;
    private final Difficulty difficulty;
    private final Double gameLength;
    private final Platform platform;
    private final Boolean completed;
    private final Boolean replayable;


    public SaveReviewDTO(String title, String content, Integer rating, Difficulty difficulty, Double gameLength, Platform platform, Boolean completed, Boolean replayable) {
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.difficulty = difficulty;
        this.gameLength = gameLength;
        this.platform = platform;
        this.completed = completed;
        this.replayable = replayable;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Integer getRating() {
        return rating;
    }

    public Double getGameLength() {
        return gameLength;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public Boolean getReplayable() {
        return replayable;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Platform getPlatform() {
        return platform;
    }
}
