package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.servicesinterfaces.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ImageController {

    private final ImageService imageService;
    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @RequestMapping(value="/images/{id:[A-Za-z0-9]{16}}",method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@PathVariable String id) {
        Image image = imageService.getImage(id);
        HttpHeaders headers = new HttpHeaders();
        if(image == null){
            return new ResponseEntity<>(null, headers, HttpStatus.NOT_FOUND);
        }
        headers.setContentType(MediaType.parseMediaType(image.getMediaType()));
        return new ResponseEntity<>(image.getImage(), headers, HttpStatus.OK);
    }

}

