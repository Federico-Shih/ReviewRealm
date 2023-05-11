package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.dtos.*;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.enums.ReviewFeedback;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.helpers.CommonRowMappers;
import ar.edu.itba.paw.persistence.helpers.QueryBuilder;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import ar.edu.itba.paw.persistenceinterfaces.PaginationDao;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import org.javatuples.Pair;

@Repository
public class ReviewDaoImpl implements ReviewDao, PaginationDao<ReviewFilter> {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsertReview;

    private final SimpleJdbcInsert jdbcInsertFeedback;
    private final GameDao gameDao;

    @Autowired
    public ReviewDaoImpl(DataSource ds, GameDao gameDao) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsertReview = new SimpleJdbcInsert(ds).withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        this.jdbcInsertFeedback = new SimpleJdbcInsert(ds).withTableName("reviewfeedback");
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
        args.put("likes", 0);
        args.put("dislikes", 0);
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
        return new Review(id.longValue(), author, title, content, LocalDateTime.now(), rating, reviewedGame, difficulty, gameLength, platform, completed, replayable,null,0L);
    }
    private final static ResultSetExtractor<List<Review>> REVIEW_MAPPER = (resultSet) -> {
        Map<Long,Review> reviews = new LinkedHashMap<>();
        while(resultSet.next()){
            Long id = resultSet.getLong("id");
            reviews.putIfAbsent(id, CommonRowMappers.REVIEW_ROW_MAPPER.mapRow(resultSet, resultSet.getRow()));
            Review review = reviews.get(id);
            review.getReviewedGame().getGenres().add(CommonRowMappers.GAME_GENRE_ROW_MAPPER.mapRow(resultSet, resultSet.getRow()));
            String feedback = resultSet.getString("feedback");
            if(review.getFeedback() == null && feedback != null) {
                review.setFeedback(ReviewFeedback.valueOf(feedback));
            }
        }
        return new ArrayList<>(reviews.values());
    };

    @Override
    public Optional<Review> findById(Long id, Long activeUserId) {
        Optional<Review> review = jdbcTemplate.query("SELECT * FROM reviews " +
                "JOIN games as g ON g.id = reviews.gameid " +
                "JOIN users as u ON u.id = reviews.authorid " +
                "LEFT OUTER JOIN reviewfeedback as rf ON rf.reviewid = reviews.id " +
                "LEFT OUTER JOIN genreforgames as gg ON gg.gameid = reviews.gameid " +
                "WHERE reviews.id = ?", REVIEW_MAPPER, id)
                .stream()
                .findFirst();
//        if (review.isPresent()) {
//            Review existentReview = review.get();
//            if(activeUserId != null) {
//                ReviewFeedback feedback = getReviewFeedback(id, activeUserId);
//                existentReview.setFeedback(feedback);
//            }
//            existentReview.getReviewedGame().setGenres(gameDao.getGenresByGame(existentReview.getReviewedGame().getId()));
//        }
        return review;
    }
    @Override
    public Paginated<Review> findAll(Page pagination, ReviewFilter filter, Ordering<ReviewOrderCriteria> ordering, Long activeUserId) {
        int totalPages = getPageCount(filter, pagination.getPageSize());
        if (pagination.getPageNumber() > totalPages || pagination.getPageNumber() <= 0) {
            return new Paginated<>(pagination.getPageNumber(), pagination.getPageSize(), totalPages, new ArrayList<>());
        }
        QueryBuilder query = new QueryBuilder();

         query = query
                .withList("gg.genreid", filter.getFilterGameGenres())
                .withList("ap.genreid", filter.getAuthorPreferences())
                .withSimilar("r.content", filter.getReviewContent())
                 .OR()
                 .withSimilar("r.title", filter.getReviewContent())
                .withList("r.authorid", filter.getAuthors())
                .withExact("r.gameid", filter.getGameId());
        List<Object> preparedArgs = new ArrayList<>(query.toArguments());
        preparedArgs.add(pagination.getPageSize());
        preparedArgs.add(pagination.getOffset());
        List<Review> reviews = jdbcTemplate.query(
                "SELECT "+getTableColumnString()+" FROM " +
                        toTableString(filter)+
                        query.toQuery()+
                        toOrderString(ordering) +
                        " LIMIT ? OFFSET ?"
                , CommonRowMappers.REVIEW_ROW_MAPPER, preparedArgs.toArray());
        if( activeUserId != null) {
            List<Pair<Long,ReviewFeedback>> reviewFeedbacks = jdbcTemplate.query("SELECT * FROM reviewfeedback WHERE userid = ?",
                    (rs, rowNum) -> new Pair<>(rs.getLong("reviewid"), ReviewFeedback.valueOf(rs.getString("feedback"))), activeUserId);
            for (Review review: reviews){
                review.getReviewedGame().setGenres(gameDao.getGenresByGame(review.getReviewedGame().getId()));
                review.setFeedback(reviewFeedbacks.stream().filter(pair -> pair.getValue0().equals(review.getId())).findFirst().map(Pair::getValue1).orElse(null));
            }
        }else{
            for (Review review: reviews){
                review.getReviewedGame().setGenres(gameDao.getGenresByGame(review.getReviewedGame().getId()));
            }
        }

        return new Paginated<>(pagination.getPageNumber(), pagination.getPageSize(), totalPages, reviews);
    }
    private String getTableColumnString(){
        return "distinct r.id, r.authorid, r.gameid, r.title, r.content, r.createddate, r.rating, r.difficulty, r.gamelength, r.platform, r.completed, r.replayability,r.likes,r.dislikes," +
                " u.id, u.email,u.username, g.name, g.description, g.imageid,g.publisher, g.developer, g.publishdate, g.ratingsum, g.reviewcount";
    }
    @Override
    public Long count(ReviewFilter filter) {
        QueryBuilder query = new QueryBuilder()
                .withList("gg.genreid", filter.getFilterGameGenres())
                .withList("ap.genreid", filter.getAuthorPreferences())
                .withSimilar("r.content", filter.getReviewContent())
                .withList("r.authorid", filter.getAuthors());
        List<Object> preparedArgs = new ArrayList<>(query.toArguments());
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT r.id) FROM " +
                        toTableString(filter)+
                        query.toQuery(),
                preparedArgs.toArray(),
                Long.class
        );
    }

    @Override
    public boolean deleteReview(Long id) {
        return jdbcTemplate.update("DELETE FROM reviews WHERE id = ?", id) == 1;
    }

    @Override
    public void updateFavGames(long userId, Long idReviewToAdd, Long idGameToAdd, Optional<Long> optIdToDelete) {
        optIdToDelete.ifPresent(reviewIdToDelete -> jdbcTemplate.update("DELETE FROM favoritegames WHERE userid = ? AND reviewId = ?", userId, reviewIdToDelete));

        jdbcTemplate.update("INSERT INTO favoritegames(gameid, userid,reviewId)  VALUES (?,?,?)",
                idGameToAdd, userId, idReviewToAdd);

    }

    @Override
    public List<Review> getBestReviews(long userId) {
        return jdbcTemplate.query("SELECT * FROM reviews INNER JOIN favoritegames f on reviews.id = f.reviewid " +
                "INNER JOIN users u on u.id = f.userid INNER JOIN games g on f.gameid = g.id " +
                "WHERE authorid = ?", CommonRowMappers.REVIEW_ROW_MAPPER, userId);
    }

    @Override
    public ReviewFeedback getReviewFeedback(Long reviewId, Long userId) {
        return jdbcTemplate.query("SELECT * FROM reviewfeedback WHERE userid = ? and reviewid = ?",
                        (rs, rowNum) -> ReviewFeedback.valueOf(rs.getString("feedback")),userId, reviewId)
                .stream().findFirst().orElse(null);
    }

    @Override
    public boolean editReviewFeedback(Long reviewId, Long userId, ReviewFeedback oldFeedback, ReviewFeedback feedback) {
        if(oldFeedback == feedback){
            return false;
        }
        boolean response = jdbcTemplate.update("UPDATE reviewfeedback SET feedback = ? WHERE userid = ? and reviewid = ?",
                feedback.name(), userId, reviewId) == 1 ;
        if(response){
            response = jdbcTemplate.update("UPDATE reviews SET likes = coalesce(likes,0) + ?, dislikes = coalesce(dislikes,0) + ? WHERE id = ?",
                    (oldFeedback == ReviewFeedback.LIKE ? -1 : 1),
                    (oldFeedback == ReviewFeedback.DISLIKE ? -1 : 1),
                    reviewId) == 1;
        }
        return response;
    }

    @Override
    public boolean addReviewFeedback(Long reviewId, Long userId, ReviewFeedback feedback) {
        Map<String, Object> args = new HashMap<>();
        args.put("reviewId",reviewId);
        args.put("userId",userId);
        args.put("feedback",feedback.name());
        jdbcInsertFeedback.execute(args);
         return jdbcTemplate.update("UPDATE reviews SET likes = coalesce(likes,0) + ?, dislikes = coalesce(dislikes,0) + ? WHERE id = ?",
                    (feedback == ReviewFeedback.LIKE ? 1: 0),
                    (feedback == ReviewFeedback.DISLIKE ? 1 : 0),
                    reviewId) == 1;
    }

    @Override
    public boolean deleteReviewFeedback(Long reviewId, Long userId, ReviewFeedback oldFeedback){
        boolean response = jdbcTemplate.update("DELETE FROM reviewfeedback WHERE userid = ? and reviewid = ?",
                userId, reviewId) == 1 ;
        if(response){
            response = jdbcTemplate.update("UPDATE reviews SET likes = coalesce(likes,0) + ?, dislikes = coalesce(dislikes,0) + ? WHERE id = ?",
                    (oldFeedback == ReviewFeedback.LIKE ? -1 : 0),
                    (oldFeedback == ReviewFeedback.DISLIKE ? -1 : 0),
                    reviewId) == 1;
        }
        return response;
    }

    private String toTableString(ReviewFilter filter) {
        StringBuilder str = new StringBuilder();
        str.append("reviews as r JOIN games as g ON g.id = r.gameid JOIN users as u ON u.id = r.authorid ");
        if (filter.getFilterGameGenres() != null && !filter.getFilterGameGenres().isEmpty() ) {
            str.append("JOIN genreforgames as gg ON r.gameid = gg.gameid ");
        }
        if (filter.getAuthorPreferences() != null && !filter.getAuthorPreferences().isEmpty()) {
            str.append("JOIN genreforusers as ap ON ap.userid = r.authorid ");
        }
        return str.toString();
    }

    private String toOrderString(Ordering<ReviewOrderCriteria> order) {
        return " ORDER BY " +
                order.getOrderCriteria().getAltName()+
                " " +
                order.getOrderDirection().getAltName();
    }
}
