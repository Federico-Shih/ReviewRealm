package ar.edu.itba.paw.webapp.forms;

import ar.edu.itba.paw.annotations.ValidMediaSize;
import ar.edu.itba.paw.dtos.saving.SubmitGameDTO;
import ar.edu.itba.paw.annotations.ValidMediaType;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.List;

public class SubmitGameForm {
    @Size(min = 1, max = 80)
    private String name;
    @Size(max = 300)
    private String description;
    @Size(min = 1, max = 50)
    private String developer;
    @Size(min = 1, max = 50)
    private String publisher;
    private List<Integer> genres;

    @ValidMediaType(value = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE}, message = "invalid.mediatype")
    @ValidMediaSize(value = 64 * 1024 * 1024)
    private MultipartFile image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public List<Integer> getGenres() {
        return genres;
    }

    public void setGenres(List<Integer> genres) {
        this.genres = genres;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public SubmitGameDTO toSubmitDTO() throws IOException {
        return new SubmitGameDTO(name, description, developer, publisher, genres, image.getBytes(), image.getContentType());
    }
}
