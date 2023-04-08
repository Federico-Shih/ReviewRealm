package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.dtos.ReviewFilter;
import ar.edu.itba.paw.dtos.ReviewOrderCriteria;
import ar.edu.itba.paw.models.Genre;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.*;

@Repository
public class ReviewDaoImpl implements ReviewDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsertReview;

    private final static RowMapper<Review> REVIEW_ROW_MAPPER = ((resultSet, i) -> new Review(
            resultSet.getLong("id"),
            new User(resultSet.getLong("authorId"), resultSet.getString("u.email"), resultSet.getString("u.password")),
            resultSet.getString("title"),
            resultSet.getString("content"),
            resultSet.getDate("created").toLocalDate(),
            resultSet.getInt("rating"),
            new Game(
                    resultSet.getLong("gameId"),
                    resultSet.getString("g.name"),
                    resultSet.getString("g.description"),
                    resultSet.getString("g.developer"),
                    resultSet.getString("g.publisher"),
                    resultSet.getString("g.imageUrl"),
                    new ArrayList<>(),
                    resultSet.getDate("publishDate").toLocalDate()
            )
    ));
    @Autowired
    public ReviewDaoImpl(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsertReview = new SimpleJdbcInsert(ds).withTableName("reviews")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Review create(String title, String content, Integer rating, Game reviewedGame, User author) {
        Map<String, Object> args = new HashMap<>();
        args.put("title", title);
        args.put("content", content);
        args.put("rating", rating);
        args.put("gameId", reviewedGame.getId());
        args.put("authorId", author.getId());
        final Number id = jdbcInsertReview.executeAndReturnKey(args);
        return new Review(id.longValue(), author, title, content, LocalDate.now(), rating, reviewedGame);
    }

    @Override
    public Optional<Review> getById(Long id) {
        return jdbcTemplate.query("SELECT * FROM reviews " +
                "JOIN games as g ON g.id = reviews.gameid " +
                "JOIN users as u ON u.id = reviews.authorid " +
                "WHERE id = ?", REVIEW_ROW_MAPPER, id)
                .stream()
                .findFirst();
    }

    private String toReviewOrderString(ReviewOrderCriteria criteria) {
        if (criteria.equals(ReviewOrderCriteria.REVIEW_DATE)) {
            return "createddate";
        }
        return "rating";
    }



    private String toReviewFilterString(ReviewFilter filter) {
        String gamesAmount = String.join(",", Collections.nCopies(filter.getReviewerPreferencesFilter().size(), "?"));
        StringBuilder str = new StringBuilder();
        if (!filter.getGameGenresFilter().isEmpty() || !filter.getReviewerPreferencesFilter().isEmpty()) {
            str.append("WHERE ");
            // TODO: filters
        }
        str.append("ORDER BY ");
        str.append(toReviewOrderString(filter.getReviewOrderCriteria()));
        str.append(" ");
        str.append(filter.getOrderDirection().getAltName());
        return str.toString();
    }
    @Override
    public List<Review> getAll(ReviewFilter filter) {
        return jdbcTemplate.query(
                "SELECT * FROM reviews " +
                        "JOIN games as g ON g.id = reviews.gameid " +
                        "JOIN users as u ON u.id = reviews.authorid " +
                        toReviewFilterString(filter)
        , REVIEW_ROW_MAPPER, filter.getReviewerPreferencesFilter().toArray());
    }
}
