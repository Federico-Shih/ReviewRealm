package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.servicesinterfaces.ImageService;
import ar.edu.itba.paw.webapp.controller.cache.CacheHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

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
    public Response getImage(@PathParam("id") String id) {
        Image image = imageService.getImage(id);
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

