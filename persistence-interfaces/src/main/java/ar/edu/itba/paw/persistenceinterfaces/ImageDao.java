package ar.edu.itba.paw.persistenceinterfaces;

import ar.edu.itba.paw.models.Image;

public interface ImageDao {

    Image uploadImage(byte[] image, String mediaType);

    Image getImage(String id);
}
