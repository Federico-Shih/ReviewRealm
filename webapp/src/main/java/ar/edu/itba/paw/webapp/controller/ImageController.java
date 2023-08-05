package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.servicesinterfaces.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/images")
@Component
public class ImageController {

    private final ImageService imageService;
    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @Path("/{id:[A-Za-z0-9]{16}}")
    @GET
    public Response getImage(@PathVariable String id) {
        Image image = imageService.getImage(id);
        HttpHeaders headers = new HttpHeaders();
        if(image == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        headers.setContentType(MediaType.parseMediaType(image.getMediaType()));
        return Response.ok(image.getImage(), image.getMediaType()).build();
    }

}

