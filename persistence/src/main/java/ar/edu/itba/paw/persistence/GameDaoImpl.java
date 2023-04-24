package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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

    private final static RowMapper<Game> GAME_ROW_MAPPER = (resultSet, i) ->
            new Game(resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("description"),
            resultSet.getString("developer"),
            resultSet.getString("publisher"),
            resultSet.getString("imageUrl"),
            new ArrayList<>(),// despues manualmente tenes que formar los generos que tiene
            resultSet.getTimestamp("publishDate").toLocalDateTime().toLocalDate());
    private final static RowMapper  <Genre> GAME_GENRE_ROW_MAPPER = (resultSet, i) -> {
        Optional<Genre> genre = Genre.getById(resultSet.getInt("genreId"));
        if (!genre.isPresent()) throw new IllegalStateException();
        return genre.get();
    };

    private final static RowMapper<Review> REVIEW_ROW_MAPPER = ((resultSet, i) -> {
        String difficulty = resultSet.getString("difficulty");
        String platform = resultSet.getString("platform");

        return new Review(
                resultSet.getLong("id"),
                new User(
                        resultSet.getLong("authorId"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        "-"),
                resultSet.getString("title"),
                resultSet.getString("content"),
                resultSet.getTimestamp("createddate").toLocalDateTime(),
                resultSet.getInt("rating"),
                new Game(
                        resultSet.getLong("gameId"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("developer"),
                        resultSet.getString("publisher"),
                        resultSet.getString("imageUrl"),
                        new ArrayList<>(),
                        resultSet.getTimestamp("publishDate").toLocalDateTime().toLocalDate()
                ),
                difficulty != null ? Difficulty.valueOf(difficulty.toUpperCase()) : null,
                resultSet.getDouble("gamelength"),
                platform != null ? Platform.valueOf(platform.toUpperCase()) : null,
                resultSet.getBoolean("completed"),
                resultSet.getBoolean("replayability")
        );
    });
    private final static RowMapper<Double> AVERAGE_REVIEW_RATING_ROW_MAPPER = (resultSet, i) -> resultSet.getDouble("average");
    @Override
    public Paginated<GameData> getAll(int page, Integer pageSize) {
        Long totalGames = getTotalAmountOfGames();
        int pages = (int) Math.ceil(totalGames/pageSize.doubleValue());
        if(page > pages){
            return new Paginated<>(page,pageSize,pages,new ArrayList<>());
        }
        Long offset = (long) (page-1) * 10;
        List<Game> games = jdbcTemplate.query("SELECT * FROM games LIMIT ? OFFSET ?",GAME_ROW_MAPPER,pageSize,offset);
        List<GameData> gameData = new ArrayList<>();
        for(Game g : games){
            g.setGenres(this.getGenresByGame(g.getId()));
            gameData.add(new GameData(g,getAverageReviewRatingById(g.getId())));
        }
        return new Paginated<>(page,pageSize,pages,gameData);
    }

    @Override
    public Game create(String name,String description,String developer, String publisher, String imageUrl, List<Genre> genres, LocalDate publishDate) {
        Map<String,Object> args = new HashMap<>();
        args.put("name",name);
        args.put("description",description);
        args.put("developer",developer);
        args.put("publisher",publisher);
        args.put("imageUrl",imageUrl);
        args.put("publishDate", new Timestamp(publishDate.toEpochDay()));

        final Number id = jdbcInsertGames.executeAndReturnKey(args);

        for(Genre g : genres){
            args = new HashMap<>();

            args.put("gameId",id.longValue());
            args.put("genreId",g.getId());

            jdbcInsertGenreForGames.execute(args);
        }
        return new Game(id.longValue(),name,description,developer,publisher,imageUrl,genres,publishDate);
    }

    @Override
    public Optional<Game> getById(Long id) {
        Optional<Game> game = jdbcTemplate.query("SELECT * FROM games where id = ?",GAME_ROW_MAPPER,id).stream().findFirst();
        //Busco los genres directamente, no paso por genreID
        game.ifPresent(value -> value.setGenres(this.getGenresByGame(id)));
        return game;
    }

    @Override
    public List<Review> getReviewsById(Long id) {
       return jdbcTemplate.query("SELECT * FROM reviews " +
                "JOIN games as g ON g.id = reviews.gameid " +
                "JOIN users as u ON u.id = reviews.authorid " +
                "WHERE g.id = ?", REVIEW_ROW_MAPPER, id);
        //No buscamos los generos de lo juegos pues por ahora no se usan en ningun momento y es mejor hacer menos cant de querys
    }

    @Override
    public Double getAverageReviewRatingById(Long id) {
        return jdbcTemplate.query("SELECT avg(rating) as average FROM reviews " +
                "WHERE gameid = ? group by gameid", AVERAGE_REVIEW_RATING_ROW_MAPPER, id).stream().findFirst().orElse(0.0);
    }

    @Override
    public List<Genre> getGenresByGame(Long id) {
        return jdbcTemplate.query("SELECT * FROM genreforgames g " +
                "WHERE g.gameid = ?",GAME_GENRE_ROW_MAPPER,id);
    }

    private Long getTotalAmountOfGames(){
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM games",Long.class);
    }

    @Override
    public List<Game> getFavoriteGamesFromUser(long userId) {
        return jdbcTemplate.query("SELECT * FROM games where id = 1 OR id = 2 OR id = 3",GAME_ROW_MAPPER);
    }
}
