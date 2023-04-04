package ar.edu.itba.paw.models;

import java.time.LocalDate;
import java.util.List;

public class Game {
    private final Integer id;
    private final String name;
    private final String developer;
    private final String publisher;
    private final String imageUrl;
    private final List<Genre> genres;
    private final LocalDate publishDate;

    public Game(Integer id, String name, String developer, String publisher, String imageUrl, List<Genre> genres, LocalDate publishDate) {
        this.id = id;
        this.name = name;
        this.developer = developer;
        this.publisher = publisher;
        this.imageUrl = imageUrl;
        this.genres = genres;
        this.publishDate = publishDate;
    }

    public Integer getId() {
        return id;
    }

    public String getDeveloper() {
        return developer;
    }

    public String getName() {
        return name;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }
}
