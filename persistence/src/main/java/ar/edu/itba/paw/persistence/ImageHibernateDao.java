package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistenceinterfaces.ImageDao;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class ImageHibernateDao implements ImageDao {
    private final static int ID_LENGTH = 16;
    @PersistenceContext
    private EntityManager em;

    @Override
    public Image uploadImage(byte[] image, String mediaType) {
        Image img = new Image(generateId(), mediaType, image);
        em.persist(img);
        em.flush();
        return img;
    }

    @Override
    public Image getImage(String id) {
        return em.find(Image.class, id);
    }

    private String generateId() {
        return RandomStringUtils.randomAlphanumeric(ID_LENGTH);
    }
}

