package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.servicesinterfaces.ImageService;
import ar.edu.itba.paw.webapp.controller.cache.CacheHelper;
import ar.edu.itba.paw.webapp.exceptions.CustomRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Path("images")
@Component
public class ImageController {

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
                throw new CustomRuntimeException(Response.Status.INTERNAL_SERVER_ERROR, "image.resizing.error");
            }
        }
        HttpHeaders headers = new HttpHeaders();
        if(image == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        headers.setContentType(MediaType.parseMediaType(image.getMediaType()));

        final Response.ResponseBuilder responseBuilder = Response.ok(image.getImage(), image.getMediaType());

        CacheHelper.unconditionalCache(responseBuilder, 86400);

        return responseBuilder.build();
    }


}

