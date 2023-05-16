package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.models.Image;
import java.net.URL;

public interface ImageService {

    Image uploadImage(byte[] image, String extension);

    Image uploadImage(URL url);

    Image getImage(String id);
}
