package ar.edu.itba.paw.models;

import java.util.Date;
import java.util.List;

public class Game {
    private final Integer id;
    private final String name;
    private final String developer;
    private final String publisher;
    private final String imageUrl;
    private final List<Category> categories;
    private final Date publishDate;

    public Game(Integer id, String name, String developer, String publisher, String imageUrl, List<Category> categories, Date publishDate) {
        this.id = id;
        this.name = name;
        this.developer = developer;
        this.publisher = publisher;
        this.imageUrl = imageUrl;
        this.categories = categories;
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

    public List<Category> getCategories() {
        return categories;
    }

    public Date getPublishDate() {
        return publishDate;
    }
}
