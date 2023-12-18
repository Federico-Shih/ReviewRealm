package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.servicesinterfaces.ImageService;
import ar.edu.itba.paw.webapp.controller.cache.CacheHelper;
import ar.edu.itba.paw.webapp.exceptions.CustomRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("images")
@Component
public class ImageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageController.class);
    private final ImageService imageService;
    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @Path("/{id:[A-Za-z0-9]{16}}")
    @GET
    public Response getImage(@PathParam("id") String id,
                             @Valid @Min( value = 1, message = "Size.getImage.height") @QueryParam("height") Integer height,
                             @Valid @Min( value = 1, message = "Size.getImage.width") @QueryParam("width") Integer width) {
        Image image;
        if(height == null && width == null) {
            image = imageService.getImage(id);
        }else{
            try{
                image = imageService.getImage(id, height, width);
            }catch (IOException e){
                LOGGER.error("Error resizing image", e);
                throw new CustomRuntimeException(Response.Status.INTERNAL_SERVER_ERROR, "image.resizing.error");
            }
        }
        if(image == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        final Response.ResponseBuilder responseBuilder = Response.ok(image.getImage(), image.getMediaType());
        // Uses cache busting
        CacheHelper.unconditionalCache(responseBuilder, 31536000);
        return responseBuilder.build();
    }


}

