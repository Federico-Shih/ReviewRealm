package ar.edu.itba.paw.models;

import ar.edu.itba.paw.converters.GenreAttributeConverter;
import ar.edu.itba.paw.converters.LocalDateConverter;
import ar.edu.itba.paw.enums.Genre;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "games")
public class Game {
    @Transient
    private static final String IMAGE_PATH = "/images/";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "games_id_seq")
    @SequenceGenerator(sequenceName = "games_id_seq", name = "games_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "developer", length = 100, nullable = false)
    private String developer;

    @Column(name = "publisher", length = 100, nullable = false)
    private String publisher;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "imageid", referencedColumnName = "id")
    private Image image;

    @ElementCollection(targetClass = Genre.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "genreforgames", joinColumns = @JoinColumn(name = "gameid", referencedColumnName = "id"))
    @Column(name = "genreid")
    @Convert(converter = GenreAttributeConverter.class)
    private List<Genre> genres;

    @Column(name = "publishdate", nullable = false)
    @Convert(converter = LocalDateConverter.class)
    private LocalDate publishDate;

    @Column(name = "reviewcount")
    private Integer reviewCount = 0;

    @Column(name = "ratingsum")
    private Integer ratingSum = 0;

    @Column(name = "suggestion")
    private Boolean suggestion = false;

    @OneToMany(mappedBy = "reviewedGame", fetch = FetchType.LAZY)
    private List<Review> reviews;

    @Formula(value = "case when reviewCount = 0 then 0 else ratingSum/reviewCount end")
    private Double averageRating;

    public Game(String name, String description, String developer, String publisher, Image image, List<Genre> genres, LocalDate publishDate, Boolean suggestion) {
        this.name = name;
        this.description = description;
        this.developer = developer;
        this.publisher = publisher;
        this.image = image;
        this.genres = genres;
        this.publishDate = publishDate;
        this.suggestion = suggestion;
        this.reviewCount = 0;
        this.ratingSum = 0;
    }


    public Game(long id,
                String name,
                String description,
                String developer,
                String publisher,
                boolean suggestion,
                String imageid,
                String releaseDate,
                int ratingSum,
                int ratingCount,
                List<Genre> genres) {
        // For testing
        this.id = id;
        this.suggestion = suggestion;
        this.ratingSum = ratingSum;
        this.reviewCount = ratingCount;
        this.name = name;
        this.description = description;
        this.developer = developer;
        this.publisher = publisher;
        byte[] imageBytes = new byte[0];
        this.image = new Image(imageid, "jpg", imageBytes);
        this.publishDate = LocalDate.parse(releaseDate);
        this.genres = genres;
    }

    public Game(long id, String name, String description, String developer, String publisher, LocalDate publishDate, int ratingsum, int reviewcount) {
        // for testing
        this.id = id;
        this.name = name;
        this.description = description;
        this.developer = developer;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.ratingSum = ratingsum;
        this.reviewCount = reviewcount;
    }

    protected Game() {
        // Just for Hibernate
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
        return IMAGE_PATH + image.getId();
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public Double getAverageRating() {
        return reviewCount != 0 ? (double) ratingSum / reviewCount : 0;
    }

    public String getAverageRatingString() {
        return String.format("%.2f", getAverageRating());
    }

    public Image getImage() {
        return image;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public boolean getSuggestion() {
        return suggestion;
    }


    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(id, game.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Integer getRatingSum() {
        return ratingSum;
    }

    public void setRatingSum(Integer ratingSum) {
        this.ratingSum = ratingSum;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setSuggestion(boolean suggestion) {
        this.suggestion = suggestion;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

}
