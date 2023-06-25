package ar.edu.itba.paw.dtos.saving;

import java.time.LocalDate;
import java.util.List;

public class SubmitGameDTO {
    private final String name;
    private final String description;
    private final String developer;
    private final String publisher;
    private final List<Integer> genres;
    private final byte[] imageData;
    private final String mediatype;

    private final LocalDate releaseDate;

    public SubmitGameDTO(String name, String description, String developer, String publisher, List<Integer> genres, byte[] imageData, String mediatype, LocalDate releaseDate) {
        this.name = name;
        this.description = description;
        this.developer = developer;
        this.publisher = publisher;
        this.genres = genres;
        this.imageData = imageData;
        this.mediatype = mediatype;
        this.releaseDate = releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getDeveloper() {
        return developer;
    }

    public String getPublisher() {
        return publisher;
    }

    public List<Integer> getGenres() {
        return genres;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public String getMediatype() {
        return mediatype;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }
}
