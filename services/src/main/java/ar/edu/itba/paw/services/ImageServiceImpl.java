package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistenceinterfaces.ImageDao;
import ar.edu.itba.paw.servicesinterfaces.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

@Service
public class ImageServiceImpl implements ImageService {
    private final ImageDao imageDao;

    @Autowired
    public ImageServiceImpl(ImageDao imageDao){
        this.imageDao = imageDao;
    }



    @Override
    public Image uploadImage(URL url){ /// TODO
        BufferedImage img;
        try {
            img = ImageIO.read(url);
        } catch (IOException e) {
            return null; // LOG ERROR NO SE PUDO LEER throw todo
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "jpg", baos);
            baos.flush();
        } catch (IOException e) {
            return null;  // LOG ERROR NO SE PUDO ESCRIBIR throw todo
        }
        byte[] imageInByte = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            return null;  // LOG ERROR NO SE PUDO CERRAR todo
        }
        return imageDao.uploadImage(imageInByte,"image/jpg");
    }

    @Override
    public Image uploadImage(byte[] image, String extension) {
        return imageDao.uploadImage(image, extension);
    }

    @Override
    public Image getImage(String id) {
        return imageDao.getImage(id);
    }
}
