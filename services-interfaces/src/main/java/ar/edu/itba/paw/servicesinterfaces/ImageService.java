package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.models.Image;

import java.io.IOException;
import java.net.URL;

public interface ImageService {

    Image uploadImage(byte[] image, String extension);

    Image uploadImage(URL url);

    Image getImage(String id);

    Image getImage(String id, Integer height, Integer width) throws IOException;
}
