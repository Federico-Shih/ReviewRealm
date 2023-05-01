package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.dtos.Filter;
import ar.edu.itba.paw.dtos.GameOrderCriteria;
import ar.edu.itba.paw.dtos.OrderDirection;
import ar.edu.itba.paw.dtos.ReviewOrderCriteria;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Repository
public class GameDaoImpl implements GameDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsertGames;

    private final SimpleJdbcInsert jdbcInsertGenreForGames;

    @Autowired
    public GameDaoImpl(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsertGames = new SimpleJdbcInsert(ds).withTableName("games")
                .usingGeneratedKeyColumns("id");
        this.jdbcInsertGenreForGames = new SimpleJdbcInsert(ds).withTableName("genreForGames");
    }

    private final static RowMapper  <Genre> GAME_GENRE_ROW_MAPPER = (resultSet, i) -> {
        Optional<Genre> genre = Genre.getById(resultSet.getInt("genreId"));
        if (!genre.isPresent()) throw new IllegalStateException();
        return genre.get();
    };

    @Override
    public Game create(String name,String description,String developer, String publisher, String imageid, List<Genre> genres, LocalDate publishDate) {
        Map<String,Object> args = new HashMap<>();
        args.put("name",name);
        args.put("description",description);
        args.put("developer",developer);
        args.put("publisher",publisher);
        args.put("imageid",imageid);
        args.put("publishDate", Timestamp.valueOf(publishDate.atStartOfDay()));

        final Number id = jdbcInsertGames.executeAndReturnKey(args);

        for(Genre g : genres){
            args = new HashMap<>();

            args.put("gameId",id.longValue());
            args.put("genreId",g.getId());

            jdbcInsertGenreForGames.execute(args);
        }
        return new Game(id.longValue(),name,description,developer,publisher,CommonRowMappers.IMAGE_PREFIX + imageid,genres,publishDate,0d);
    }

    @Override
    public Optional<Game> getById(Long id) {
        Optional<Game> game = jdbcTemplate.query("SELECT * FROM games where id = ?",CommonRowMappers.GAME_ROW_MAPPER,id).stream().findFirst();
        //Busco los genres directamente, no paso por genreID
        game.ifPresent(value -> value.setGenres(this.getGenresByGame(id)));
        return game;
    }

    @Override
    public List<Review> getReviewsById(Long id) {
       return jdbcTemplate.query("SELECT * FROM reviews " +
                "JOIN games as g ON g.id = reviews.gameid " +
                "JOIN users as u ON u.id = reviews.authorid " +
                "WHERE g.id = ?", CommonRowMappers.REVIEW_ROW_MAPPER, id);
        //No buscamos los generos de lo juegos pues por ahora no se usan en ningun momento y es mejor hacer menos cant de querys
    }

    @Override
    public Double getAverageReviewRatingById(Long id) {
        return jdbcTemplate.query("SELECT cast(g.ratingsum as real) / NULLIF(g.reviewcount,0) as average FROM games g WHERE id = ?",
                (resultSet, i) -> resultSet.getDouble("average"), id).stream().findFirst().orElse(0.0);
    }

    @Override
    public void addNewReview(Long gameId, Integer rating) {
        jdbcTemplate.execute("UPDATE games SET ratingsum = ratingsum + ?, reviewcount = reviewcount+1  WHERE id = ?", (PreparedStatementCallback<Object>) ps -> {
            ps.setInt(1, rating);
            ps.setLong(2, gameId);
            return ps.execute();
        });
    }

    @Override
    public void modifyReview(Long gameId, Integer oldRating, Integer newRating) {
        jdbcTemplate.execute("UPDATE games SET ratingsum = ratingsum - ? + ? WHERE id = ?", (PreparedStatementCallback<Object>) ps -> {
            ps.setInt(1, oldRating);
            ps.setInt(2, newRating);
            ps.setLong(3, gameId);
            return ps.execute();
        });
    }

    @Override
    public void deleteReview(Long gameId, Integer rating) {
        jdbcTemplate.execute("UPDATE games SET ratingsum = ratingsum - ?, reviewcount= reviewcount - 1 WHERE id = ?", (PreparedStatementCallback<Object>) ps -> {
            ps.setInt(1, rating);
            ps.setLong(2, gameId);
            return ps.execute();
        });
    }

    @Override
    public List<Genre> getGenresByGame(Long id) {
        return jdbcTemplate.query("SELECT * FROM genreforgames g " +
                "WHERE g.gameid = ?",GAME_GENRE_ROW_MAPPER,id);
    }

    private Long getTotalAmountOfGames(Filter filter){
        List<Object> preparedStatementArgs = new ArrayList<>(filter.getGameGenresFilter());
        return jdbcTemplate.queryForObject("SELECT COUNT(distinct g.id) FROM games as g "+
                toGameFilterString(filter,new ArrayList<>()),
                preparedStatementArgs.toArray(),
                Long.class);
    }

    private String toCriteriaString(GameOrderCriteria criteria) {
        if (criteria.equals(GameOrderCriteria.PUBLISH_DATE)) {
            return "publishDate";
        }
        return "name";
    }
    private String toCriteriaString(Filter filter) {
        return "ORDER BY " +
                toCriteriaString(filter.getGameOrderCriteria()) +
                " " +
                filter.getOrderDirection().getAltName();
    }

    @Override
    public Paginated<Game> getAll(int page, Integer pageSize, Filter filter, String searchQuery) {
        Long totalGames = getTotalAmountOfGames(filter);
        int totalPages = (int) Math.ceil(totalGames/pageSize.doubleValue());

        if (page > totalPages || page <= 0) {
            return new Paginated<>(page, pageSize, totalPages, new ArrayList<>());
        }
        int offset = (page - 1) * pageSize;
        List<Object> preparedStatementArgs = new ArrayList<>();

        String filterQuery = toGameFilterString(filter, preparedStatementArgs);
        if (!searchQuery.isEmpty()) {

            filterQuery += (filterQuery.isEmpty()) ? " WHERE " : " AND ";
            filterQuery += " g.name ILIKE ? ";

            preparedStatementArgs.add("%" + searchQuery + "%");
        }
        preparedStatementArgs.add(pageSize);
        preparedStatementArgs.add(offset);


        List<Game> games = jdbcTemplate.query("SELECT * FROM games as g "+
                filterQuery+
                toCriteriaString(filter)+
                " LIMIT ? OFFSET ?",CommonRowMappers.GAME_ROW_MAPPER,preparedStatementArgs.toArray());
        for(Game g : games){
            g.setGenres(this.getGenresByGame(g.getId()));
        }
        return new Paginated<>(page,pageSize,totalPages,games);
    }
    private String toGameFilterString(Filter filter, List<Object> arguments) {
        String gamesAmount = String.join(",", Collections.nCopies(filter.getGameGenresFilter().size(), "?"));
        StringBuilder str = new StringBuilder();
        if (!filter.getGameGenresFilter().isEmpty()) {
            arguments.addAll(filter.getGameGenresFilter());
            str.append("JOIN genreforgames as gg ON g.id = gg.gameid ");
            str.append("WHERE gg.genreid IN (");
            str.append(gamesAmount);
            str.append(")");
            // TODO: filters
        }
        return str.toString();
    }

    @Override
    public List<Game> getFavoriteGamesFromUser(long userId) {
        //TODO: y verificar que incluya los generos de los juegos
        return jdbcTemplate.query("SELECT * FROM games where id = 1 OR id = 2 OR id = 3",CommonRowMappers.GAME_ROW_MAPPER);
    }

    @Override
    public List<Game> getRecommendationsForUser(Long userId,List<Integer> userPreferences, Integer amount) {
        List<Object> preparedStatementArgs = new ArrayList<>();
        String filterString = toGameFilterString(new Filter(userPreferences,new ArrayList<>(),null,null,null),preparedStatementArgs);
        preparedStatementArgs.add(userId);
        preparedStatementArgs.add(amount);
        filterString += (filterString.isEmpty()) ? " WHERE " : " AND ";
        List<Game> games = jdbcTemplate.query("SELECT * " +
                "FROM games g "+ filterString +
                " NOT EXISTS(select * from reviews where reviews.gameid = g.id and reviews.authorid=?)"+
                "ORDER BY (cast(g.ratingsum as real) / coalesce(nullif(g.reviewcount,0),1)) desc LIMIT ? ",CommonRowMappers.GAME_ROW_MAPPER,preparedStatementArgs.toArray());
        for(Game g : games){
            g.setGenres(this.getGenresByGame(g.getId()));
        }
        return games;
    }

    @Override
    public Paginated<Game> getAllShort(int page, Integer pageSize, String searchQuery) {
        Filter filter = new Filter( new ArrayList<>(),new ArrayList<>(),
                null,GameOrderCriteria.NAME, OrderDirection.ASCENDING);
        Long totalGames = getTotalAmountOfGames(filter);
        int totalPages = (int) Math.ceil(totalGames/pageSize.doubleValue());

        if (page > totalPages || page <= 0) {
            return new Paginated<>(page, pageSize, totalPages, new ArrayList<>());
        }
        Integer offset = (page - 1) * pageSize;
        List<Object> preparedStatementArgs = new ArrayList<>();

        String searchFilter = "";
        if (!searchQuery.isEmpty()) {
            searchFilter += " WHERE  g.name ILIKE ? ";
            preparedStatementArgs.add("%" + searchQuery + "%");
        }
        preparedStatementArgs.add(pageSize);
        preparedStatementArgs.add(offset);


        List<Game> games = jdbcTemplate.query("SELECT * FROM games as g "+searchFilter+" LIMIT ? OFFSET ?",CommonRowMappers.GAME_ROW_MAPPER,preparedStatementArgs.toArray());
        for(Game g : games){
            g.setGenres(this.getGenresByGame(g.getId()));
        }
        return new Paginated<>(page,pageSize,totalPages,games);
    }
}
