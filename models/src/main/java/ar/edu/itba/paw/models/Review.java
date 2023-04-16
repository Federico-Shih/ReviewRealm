package ar.edu.itba.paw.models;

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

    public Review(Long id, User author, String title, String content, LocalDateTime created, Integer rating, Game reviewedGame) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.content = content;
        this.created = created;
        this.rating = rating;
        this.reviewedGame = reviewedGame;
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

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", author=" + author +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", created=" + created +
                ", rating=" + rating +
                ", reviewedGame=" + reviewedGame +
                '}';
    }
}
