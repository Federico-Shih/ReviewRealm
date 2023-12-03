package ar.edu.itba.paw.webapp.controller.forms;

import ar.edu.itba.paw.dtos.saving.SubmitGameDTO;
import ar.edu.itba.paw.webapp.controller.annotations.CheckDateFormat;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentGenreList;
import ar.edu.itba.paw.webapp.controller.annotations.ValidImage;
import ar.edu.itba.paw.webapp.exceptions.CustomRuntimeException;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

public class EditGameForm {
    @Size(min = 1, max = 80,message = "Size.submitGame.name")
    @FormDataParam("name")
    private String name;
    @Size(max = 300,message = "Size.submitGame.description")
    @FormDataParam("description")
    private String description;
    @Size(min = 1, max = 50, message = "Size.submitGame.developer")
    @FormDataParam("developer")
    private String developer;
    @Size(min = 1, max = 50, message = "Size.submitGame.publisher")
    @FormDataParam("publisher")
    private String publisher;

    @ExistentGenreList()
    @FormDataParam("genres")
    private List<Integer> genres;

    @CheckDateFormat(pattern = "yyyy-mm-dd")
    @FormDataParam("releaseDate")
    private String releaseDate;


    @FormDataParam("image")
    private InputStream imageStream;

    @ValidImage
    @FormDataParam("image")
    private FormDataBodyPart imageDetails;


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

    public InputStream getImageStream() {
        return imageStream;
    }

    public void setImageStream(InputStream imageStream) {
        this.imageStream = imageStream;
    }

    public FormDataBodyPart getImageDetails() {
        return imageDetails;
    }

    public void setImageDetails(FormDataBodyPart imageDetails) {
        this.imageDetails = imageDetails;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public SubmitGameDTO toSubmitDTO(){
        byte[] imageArray = new byte[0];
        if(imageStream != null){
            try {
                imageArray = IOUtils.toByteArray(imageStream);
            } catch (IOException e) {
                throw new CustomRuntimeException(Response.Status.INTERNAL_SERVER_ERROR,"image.error");
            }
        }
        return new SubmitGameDTO(name, description, developer, publisher, genres, imageArray, imageDetails != null? imageDetails.getMediaType().toString():null, releaseDate != null? LocalDate.parse(releaseDate) : null);
    }
}
