package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistenceinterfaces.ImageDao;
import ar.edu.itba.paw.servicesinterfaces.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

@Service
public class ImageServiceImpl implements ImageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);
    private final ImageDao imageDao;

    @Autowired
    public ImageServiceImpl(ImageDao imageDao){
        this.imageDao = imageDao;
    }

    @Transactional
    @Override
    public Image uploadImage(byte[] image, String extension) {
        try {
            return imageDao.uploadImage(image, extension);
        } catch (Exception err) {
            LOGGER.error("Error creating image: {}", err.getMessage());
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Image getImage(String id) {
        return imageDao.getImage(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Image getImage(String id, Integer height, Integer width) throws IOException {
        Image image = imageDao.getImage(id);
        if(image == null){
            return null;
        }
        BufferedImage originalImg = this.bufferedImageFromByteArray(image.getImage());
        if(originalImg == null){
            return null;
        }
        height = height == null ? originalImg.getHeight() : height;
        width = width == null ? originalImg.getWidth() : width;
        BufferedImage resizedImg = new BufferedImage(width, height, originalImg.getType());
        Graphics2D g2d = resizedImg.createGraphics();
        g2d.drawImage(originalImg, 0, 0, width, height, null);
        g2d.dispose();
        return new Image(image.getId(),image.getMediaType(),this.byteArrayFromBufferedImage(resizedImg,image.getMediaType()));
    }

    private BufferedImage bufferedImageFromByteArray(byte[] imageInByte) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
        BufferedImage image = ImageIO.read(bis);
        bis.close();
        return image;
    }

    private byte[] byteArrayFromBufferedImage(BufferedImage image,String mediaType) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, mediaType.substring(mediaType.indexOf('/')+1), baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }
}
