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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.URL;

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
    //TODO BORRAR
    @RequestMapping(value="/images",method = RequestMethod.POST)
    public ModelAndView submit(@ModelAttribute("ImageForm") final ImageForm form) {
        ModelAndView mv = new ModelAndView("static-components/fileUpload");
        Image img;
        try {
            img = imageService.uploadImage(form.getFile().getBytes(),form.getFile().getContentType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mv.addObject("imageUploaded",img);
        return mv;
    }
    @RequestMapping(value = "/images",method = RequestMethod.GET)
    public ModelAndView submitForm(@ModelAttribute("ImageForm") final ImageForm form){
        return new ModelAndView("static-components/fileUpload");
    }
    @RequestMapping(value = "/images/url",method = RequestMethod.GET)
    public ModelAndView submitFormUrl(@ModelAttribute("UrlForm") final UrlForm form){
        return new ModelAndView("static-components/fileUrl");
    }

    @RequestMapping(value = "/images/url", method = RequestMethod.POST)
    public ModelAndView submitURL(@ModelAttribute("UrlForm") final UrlForm form){
        ModelAndView mv = new ModelAndView("static-components/fileUrl");
        Image img;
        try {
            img = imageService.uploadImage(new URL(form.getUrl()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mv.addObject("imageUploaded",img);
        return mv;
    }

    public static class ImageForm {
        private MultipartFile file;

        public MultipartFile getFile() {
            return file;
        }

        public void setFile(MultipartFile file) {
            this.file = file;
        }
    }
    public static class UrlForm{
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

}

