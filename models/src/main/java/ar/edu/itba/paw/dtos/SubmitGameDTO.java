package ar.edu.itba.paw.dtos;

import java.util.List;

public class SubmitGameDTO {
    private final String name;
    private final String description;
    private final String developer;
    private final String publisher;
    private final List<Integer> genres;
    private final byte[] imageData;
    private final String mediatype;

    public SubmitGameDTO(String name, String description, String developer, String publisher, List<Integer> genres, byte[] imageData, String mediatype) {
        this.name = name;
        this.description = description;
        this.developer = developer;
        this.publisher = publisher;
        this.genres = genres;
        this.imageData = imageData;
        this.mediatype = mediatype;
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
}
