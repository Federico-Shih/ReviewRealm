package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Genre;
import ar.edu.itba.paw.persistenceinterfaces.GenreDao;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsertGenres;

    @Autowired
    public GenreDaoImpl(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsertGenres = new SimpleJdbcInsert(ds).withTableName("genres")
                .usingGeneratedKeyColumns("id");

    }
    private final static RowMapper<Genre> GENRE_ROW_MAPPER = (resultSet, i) -> {
        return new Genre(resultSet.getInt("id"),resultSet.getString("name"));
    };

    @Override
    public Genre create(String name) {
        final Map<String,Object> args =  new HashMap<>();
        args.put("name",name);

        final Number id = jdbcInsertGenres.executeAndReturnKey(args);
        return new Genre(id.intValue(),name);
    }

    @Override
    public Optional<Genre> getById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM genres WHERE id = ?",GENRE_ROW_MAPPER,id).stream().findFirst();
    }

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.query("SELECT * FROM genres",GENRE_ROW_MAPPER);
    }
}
