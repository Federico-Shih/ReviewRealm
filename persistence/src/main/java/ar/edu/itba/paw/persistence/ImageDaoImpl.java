package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistenceinterfaces.ImageDao;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ImageDaoImpl implements ImageDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static int ID_LENGTH = 16;

    private final static int AMOUNT_OF_TRIES = 5;
    @Autowired
    public ImageDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds).
                withTableName("images");
    }

    @Override
    public Image uploadImage(byte[] image, String mediaType) {
        final Map<String, Object> args = new HashMap<>();

        args.put("data",image);
        args.put("mediatype",mediaType);
        boolean inserted = false;
        String id="";
        for (int i = 0; i < AMOUNT_OF_TRIES && !inserted ; i++) {
            id = generateId(ID_LENGTH);
            args.put("id",id);
            try {
                jdbcInsert.execute(args);
                inserted = true;
            } catch (Exception e) {
                // LOG ERROR
            }
        }
        return inserted ? new Image(id, mediaType, image) : null;
    }

    @Override
    public Image getImage(String id) {
        return jdbcTemplate.queryForObject("SELECT * FROM images WHERE id = ?", (rs, rowNum) -> {
            return new Image(id, rs.getString("mediatype"), rs.getBytes("data"));
        }, id);
    }
    private String generateId(Integer length){
        return RandomStringUtils.randomAlphanumeric(length);
    }
}

