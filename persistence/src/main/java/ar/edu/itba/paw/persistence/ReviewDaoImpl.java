package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.dtos.ReviewFilter;
import ar.edu.itba.paw.dtos.ReviewOrderCriteria;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class ReviewDaoImpl implements ReviewDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsertReview;
    private final GameDao gameDao;

    private final static RowMapper<Review> REVIEW_ROW_MAPPER = ((resultSet, i) -> {
        String difficulty = resultSet.getString("difficulty");
        String platform = resultSet.getString("platform");

        return new Review(
                resultSet.getLong("id"),
                new User(resultSet.getLong("authorId"), resultSet.getString("email"), "-"),
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
    @Autowired
    public ReviewDaoImpl(DataSource ds, GameDao gameDao) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsertReview = new SimpleJdbcInsert(ds).withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        this.gameDao = gameDao;
    }

    public Review create(String title,
                         String content,
                         Integer rating,
                         Game reviewedGame,
                         User author,
                         Difficulty difficulty,
                         Double gameLength,
                         Platform platform,
                         Boolean completed,
                         Boolean replayable) {
        Map<String, Object> args = new HashMap<>();
        args.put("title", title);
        args.put("content", content);
        args.put("rating", rating);
        args.put("gameId", reviewedGame.getId());
        args.put("authorId", author.getId());
        args.put("createddate", Timestamp.valueOf(LocalDateTime.now()));
        if (difficulty != null) {
            args.put("difficulty", difficulty.toString());
        }
        if (replayable != null) {
            args.put("replayability", replayable);
        }
        if (completed != null) {
            args.put("completed", completed);
        }
        if (platform != null) {
            args.put("platform", platform.toString());
        }
        if (gameLength != null) {
            args.put("gamelength", gameLength);
        }
        final Number id = jdbcInsertReview.executeAndReturnKey(args);
        return new Review(id.longValue(), author, title, content, LocalDateTime.now(), rating, reviewedGame, difficulty, gameLength, platform, completed, replayable);
    }

    @Override
    public Optional<Review> getById(Long id) {
        Optional<Review> review = jdbcTemplate.query("SELECT * FROM reviews " +
                "JOIN games as g ON g.id = reviews.gameid " +
                "JOIN users as u ON u.id = reviews.authorid " +
                "WHERE reviews.id = ?", REVIEW_ROW_MAPPER, id)
                .stream()
                .findFirst();
        if (review.isPresent()) {
            Review existentReview = review.get();
            existentReview.getReviewedGame().setGenres(gameDao.getGenresByGame(existentReview.getReviewedGame().getId()));
        }
        return review;
    }

    private String toReviewOrderString(ReviewOrderCriteria criteria) {
        if (criteria.equals(ReviewOrderCriteria.REVIEW_DATE)) {
            return "createddate";
        }
        return "rating";
    }



    private String toReviewFilterString(ReviewFilter filter) {
        String gamesAmount = String.join(",", Collections.nCopies(filter.getGameGenresFilter().size(), "?"));
        StringBuilder str = new StringBuilder();
        if (!filter.getGameGenresFilter().isEmpty()) {
            str.append("JOIN genreforgames as gg ON r.gameid = gg.gameid ");
            str.append("WHERE gg.genreid IN (");
            str.append(gamesAmount);
            str.append(" )");
            // TODO: filters
        }
        str.append(" ORDER BY ");
        str.append(toReviewOrderString(filter.getReviewOrderCriteria()));
        str.append(" ");
        str.append(filter.getOrderDirection().getAltName());
        return str.toString();
    }
    @Override
    public List<Review> getAll(ReviewFilter filter) {
        List<Review> reviews = jdbcTemplate.query(
                "SELECT DISTINCT * FROM reviews as r JOIN games as g ON g.id = r.gameid JOIN users as u ON u.id = r.authorid " +
                        toReviewFilterString(filter)
        , REVIEW_ROW_MAPPER, filter.getGameGenresFilter().toArray());
        reviews.forEach((review -> review.getReviewedGame().setGenres(gameDao.getGenresByGame(review.getReviewedGame().getId()))));
        return reviews;
    }
}
