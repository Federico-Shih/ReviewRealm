package ar.edu.itba.paw.models;

import java.time.LocalDate;
import java.util.Date;

public class Review {
    private final Integer id;
    private final User author;
    private final String title;
    private final String content;
    private final LocalDate created;
    private final Integer rating;
    private final Game reviewedGame;

    public Review(Integer id, User author, String title, String content, LocalDate created, Integer rating, Game reviewedGame) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.content = content;
        this.created = created;
        this.rating = rating;
        this.reviewedGame = reviewedGame;
    }

    public Integer getId() {
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

    public LocalDate getCreated() {
        return created;
    }

    public Integer getRating() {
        return rating;
    }

    public Game getReviewedGame() {
        return reviewedGame;
    }
}
