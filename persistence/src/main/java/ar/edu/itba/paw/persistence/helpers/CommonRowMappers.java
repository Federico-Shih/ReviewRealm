package ar.edu.itba.paw.persistence.helpers;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.enums.ReviewFeedback;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.Optional;

public interface CommonRowMappers{
    String IMAGE_PREFIX = "/images/";

    RowMapper<Game> GAME_ROW_MAPPER = (resultSet, i) ->
            new Game(resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    resultSet.getString("developer"),
                    resultSet.getString("publisher"),
                    IMAGE_PREFIX + resultSet.getString("imageid"),
                    new ArrayList<>(),// despues manualmente tenes que formar los generos que tiene
                    resultSet.getTimestamp("publishDate").toLocalDateTime().toLocalDate(),
                    (resultSet.getInt("reviewcount") != 0)?
                            (double) resultSet.getInt("ratingsum")/resultSet.getInt("reviewcount"): 0d );

    RowMapper<Review> REVIEW_ROW_MAPPER = ((resultSet, i) -> {
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
                        IMAGE_PREFIX + resultSet.getString("imageid"),
                        new ArrayList<>(),
                        resultSet.getTimestamp("publishDate").toLocalDateTime().toLocalDate(),
                        (resultSet.getInt("reviewcount") != 0)?
                                (double) resultSet.getInt("ratingsum")/resultSet.getInt("reviewcount"): 0d

                ),
                difficulty != null ? Difficulty.valueOf(difficulty.toUpperCase()) : null,
                resultSet.getDouble("gamelength"),
                platform != null ? Platform.valueOf(platform.toUpperCase()) : null,
                resultSet.getBoolean("completed"),
                resultSet.getBoolean("replayability"),
                 null,
                (resultSet.getLong("likes") - resultSet.getLong("dislikes"))
        );
    });
     RowMapper  <Genre> GAME_GENRE_ROW_MAPPER = (resultSet, i) -> {
        Optional<Genre> genre = Genre.getById(resultSet.getInt("genreId"));
        if (!genre.isPresent()) throw new IllegalStateException();
        return genre.get();
    };
}
