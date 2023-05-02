package ar.edu.itba.paw.models;

import ar.edu.itba.paw.enums.Genre;
import java.time.LocalDate;
import java.util.List;

public class Game {
    private final Long id;
    private final String name;

    private final String description;
    private final String developer;
    private final String publisher;
    private final String imageUrl;
    private List<Genre> genres;
    private final LocalDate publishDate;

    private final Double averageRating;



    public Game(Long id, String name, String description, String developer, String publisher, String imageUrl, List<Genre> genres, LocalDate publishDate, Double averageRating ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.developer = developer;
        this.publisher = publisher;
        this.imageUrl = imageUrl;
        this.genres = genres;
        this.publishDate = publishDate;
        this.averageRating = averageRating;
    }

    public String getDescription() {
        return description;
    }

    public Long getId() {
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

    public Double getAverageRating() {
        return averageRating;
    }

    public String getAverageRatingString() {
        return String.format("%.2f", averageRating);
    }

    public void setGenres(List<Genre> genres) {
        if(this.genres.isEmpty()){ // You can only change the list once as the game
            // starts out with an empty list once it is retrieved from the db
            this.genres = genres;
        }
    }
}
