package ar.edu.itba.paw.persistence.helpers;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.enums.ReportReason;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.HashSet;

public interface CommonRowMappers{
    String IMAGE_PREFIX = "/images/";

    RowMapper<Game> TEST_GAME_ROW_MAPPER = (resultSet, i) ->
            new Game(resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    resultSet.getString("developer"),
                    resultSet.getString("publisher"),
                    resultSet.getTimestamp("publishDate").toLocalDateTime().toLocalDate(),
                    resultSet.getInt("ratingsum"),
                    resultSet.getInt("reviewcount"));

    RowMapper<Review> TEST_REVIEW_MAPPER = (resultSet, i) -> {
        String difficulty = resultSet.getString("difficulty");
        String platform = resultSet.getString("platform");
        return new Review(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getString("content"),
                resultSet.getTimestamp("createddate").toLocalDateTime(),
                resultSet.getInt("rating"),
                difficulty != null ? Difficulty.valueOf(difficulty.toUpperCase()) : null,
                resultSet.getString("gamelength") != null ? resultSet.getDouble("gamelength") : null,
                platform != null ? Platform.valueOf(platform.toUpperCase()) : null,
                resultSet.getBoolean("completed"),
                resultSet.getBoolean("replayability"),
                resultSet.getLong("likes"),
                resultSet.getLong("dislikes"));
    };
    
    RowMapper<User> TEST_USER_MAPPER = ((resultSet, i) -> (
        new User(
                resultSet.getLong("id"),
                resultSet.getString("username"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getBoolean("enabled"),
                resultSet.getInt("reputation"),
                new HashSet<>(),
                resultSet.getString("language"),
                resultSet.getInt("xp"),
                new ArrayList<>(),
                resultSet.getInt("avatar")
        )
    ));
    RowMapper<Report> REPORT_ROW_MAPPER = ((resultSet, i) -> (
        new Report(
                resultSet.getLong("id"),
                ReportReason.valueOf(resultSet.getString("reason"))
        )
    ));
}
