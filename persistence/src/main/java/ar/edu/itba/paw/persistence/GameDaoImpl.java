package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.dtos.*;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.helpers.CommonRowMappers;
import ar.edu.itba.paw.persistence.helpers.QueryBuilder;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import ar.edu.itba.paw.persistenceinterfaces.PaginationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Repository
public class GameDaoImpl implements GameDao, PaginationDao<GameFilter> {
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





    private Map<String,Object> prepareArgumentsForGame(String name,String description,String developer, String publisher, String imageid, LocalDate publishDate, boolean suggested) {
        Map<String,Object> args = new HashMap<>();
        args.put("name",name);
        args.put("description",description);
        args.put("developer",developer);
        args.put("publisher",publisher);
        args.put("imageid",imageid);
        args.put("publishDate", Timestamp.valueOf(publishDate.atStartOfDay()));
        args.put("suggestion", suggested);
        return args;
    }

    private void addGenresToDB(Number id, List<Genre> genres, SimpleJdbcInsert insert) {
        Map<String,Object> args = new HashMap<>();
        for(Genre g : genres){
            args.put("gameId",id.longValue());
            args.put("genreId",g.getId());
            insert.execute(args);
        }
    }

    @Override
    public Optional<Game> create(String name,String description,String developer, String publisher, String imageid, List<Genre> genres, LocalDate publishDate, boolean suggested) {
        Map<String,Object> args = prepareArgumentsForGame(name,description,developer,publisher,imageid, publishDate, suggested);
        final Number id = jdbcInsertGames.executeAndReturnKey(args);
        addGenresToDB(id, genres, jdbcInsertGenreForGames);
        // esto quizás debería hacerse en el Service, pero quiero que create devuelva Optional
        return (suggested)? Optional.empty() : Optional.of(new Game(id.longValue(),name,description,developer,publisher, CommonRowMappers.IMAGE_PREFIX + imageid,genres,publishDate,0d));
    }


    @Override
    public Optional<Game> getById(Long id) {
        Optional<Game> game = jdbcTemplate.query("SELECT * FROM games LEFT OUTER JOIN genreforgames ON games.id = genreforgames.gameid " +
                "WHERE id = ?",CommonRowMappers.GAME_ROW_MAPPER,id).stream().findFirst();
        game.ifPresent(value -> value.setGenres(this.getGenresByGame(id)));
        return game;
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
    public boolean setSuggestedFalse(long gameId) {
        return jdbcTemplate.update("UPDATE games SET suggestion = false WHERE id = ?", gameId) == 1;
    }

    @Override
    public boolean deleteGame(long gameId) {
        // TODO: Hacer migración con delete cascade en genreforgames
        jdbcTemplate.update("DELETE FROM genreforgames WHERE gameid = ?", gameId);
        return jdbcTemplate.update("DELETE FROM games WHERE id = ?", gameId) == 1;
    }

    @Override
    public List<Genre> getGenresByGame(Long id) {
        return jdbcTemplate.query("SELECT * FROM genreforgames g " +
                "WHERE g.gameid = ?",CommonRowMappers.GENRE_ROW_MAPPER,id);
    }

    @Override
    public Paginated<Game> findAll(Page page, GameFilter filter, Ordering<GameOrderCriteria> ordering) {
        int totalPages = getPageCount(filter, page.getPageSize());
        if (page.getPageNumber() > totalPages || page.getPageNumber()<= 0) {
            return new Paginated<>(page.getPageNumber(), page.getPageSize(), totalPages, new ArrayList<>());
        }
        QueryBuilder queryBuilder = new QueryBuilder()
                .withSimilar("g.name", filter.getGameContent())
                .withList("gg.genreid", filter.getGameGenres())
                .withExact("g.publisher", filter.getPublisher())
                .withExact("g.suggestion", filter.getSuggested())
                .withExact("g.developer", filter.getDeveloper());
        List<Object> preparedStatementArgs = new ArrayList<>(queryBuilder.toArguments());

        preparedStatementArgs.add(page.getPageSize());
        preparedStatementArgs.add(page.getOffset());

        List<Game> games = jdbcTemplate.query("SELECT "+getTableColumnString()+" FROM " + toTableString(filter) +
                queryBuilder.toQuery() +
                toOrderString(ordering)+
                " LIMIT ? OFFSET ?", preparedStatementArgs.toArray(), CommonRowMappers.GAME_ROW_MAPPER);
        for(Game g : games){
            g.setGenres(this.getGenresByGame(g.getId()));
        }
        return new Paginated<>(page.getPageNumber(), page.getPageSize(), totalPages, games);
    }
    private String getTableColumnString(){
        return "distinct g.id, g.name, g.description, g.developer, g.publisher, g.imageid, g.publishdate, g.ratingsum, g.reviewcount";
    }

    @Override
    public List<Game> getFavoriteGamesFromUser(long userId) {
        return jdbcTemplate.query("SELECT * FROM favoritegames INNER JOIN games g ON favoritegames.gameid = g.id" +
                " where userid = ?",CommonRowMappers.GAME_ROW_MAPPER, userId);
    }

    @Override
    public List<Game> getRecommendationsForUser(Long userId, List<Integer> userPreferences, Integer amount) {
        QueryBuilder queryBuilder = new QueryBuilder().withList("gg.genreid", userPreferences).withExact("g.suggestion",false);
        String filterString = queryBuilder.toQuery();
        List<Object> preparedStatementArgs = new ArrayList<>(queryBuilder.toArguments());
        if (!filterString.isEmpty()) {
            filterString = filterString + " AND ";
        }
        preparedStatementArgs.add(userId);
        preparedStatementArgs.add(amount);
        List<Game> games = jdbcTemplate.query("SELECT "+ getTableColumnString()+ " ,cast(g.ratingsum as real) / coalesce(nullif(g.reviewcount,0),1) as avg " +
                "FROM games g INNER JOIN genreforgames gg on g.id = gg.gameid " + filterString +
                "NOT EXISTS(select * from reviews where reviews.gameid = g.id and reviews.authorid=?)"+
                " ORDER BY avg desc LIMIT ? ",CommonRowMappers.GAME_ROW_MAPPER,preparedStatementArgs.toArray());
        for(Game g : games){
            g.setGenres(this.getGenresByGame(g.getId()));
        }
        return games;
    }


    @Override
    public Long count(GameFilter filter) {
        QueryBuilder queryBuilder = new QueryBuilder()
                .withSimilar("g.name", filter.getGameContent())
                .withList("gg.genreid", filter.getGameGenres())
                .withExact("g.publisher", filter.getPublisher())
                .withExact("g.suggestion", false)
                .withExact("g.developer", filter.getDeveloper());

        return jdbcTemplate.queryForObject("SELECT COUNT(distinct g.id) FROM " + toTableString(filter) +
                        queryBuilder.toQuery() ,
                queryBuilder.toArguments().toArray(),
                Long.class);
    }

    private String toTableString(GameFilter filter) {
        StringBuilder str = new StringBuilder();
        str.append("games as g ");
        if (filter.getGameGenres() != null && !filter.getGameGenres().isEmpty()) {
            str.append("JOIN genreforgames as gg ON g.id = gg.gameid ");
        }
        return str.toString();
    }

    private String toOrderString(Ordering<GameOrderCriteria> order) {
        return " ORDER BY " +
                order.getOrderCriteria().getAltName()+
                " " +
                order.getOrderDirection().getAltName();
    }
}
