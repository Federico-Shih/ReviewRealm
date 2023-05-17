package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.SaveReviewDTO;
import ar.edu.itba.paw.dtos.filtering.ReviewFilter;
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
import ar.edu.itba.paw.persistence.helpers.UpdateBuilder;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import ar.edu.itba.paw.persistenceinterfaces.PaginationDao;
import ar.edu.itba.paw.persistenceinterfaces.ReviewDao;
import ar.edu.itba.paw.persistenceinterfaces.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ReviewDaoImpl implements ReviewDao, PaginationDao<ReviewFilter> {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsertReview;

    private final SimpleJdbcInsert jdbcInsertFeedback;
    private final GameDao gameDao;

    private final UserDao userDao;

    @Autowired
    public ReviewDaoImpl(DataSource ds, GameDao gameDao, UserDao userDao){
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsertReview = new SimpleJdbcInsert(ds).withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        this.jdbcInsertFeedback = new SimpleJdbcInsert(ds).withTableName("reviewfeedback");
        this.gameDao = gameDao;
        this.userDao = userDao;
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

    private final static ResultSetExtractor<Map<Long,ReviewFeedback>> USER_REVIEW_FEEDBACK_MAPPER = (resultSet) -> {
        Map<Long,ReviewFeedback> reviewFeedbackMap = new HashMap<>();
        while(resultSet.next()) {
            reviewFeedbackMap.put(resultSet.getLong("reviewid"), ReviewFeedback.valueOf(resultSet.getString("feedback")));
        }
        return reviewFeedbackMap;
    };

    @Override
    public int update(Long id, SaveReviewDTO reviewDTO) {
        UpdateBuilder updateBuilder = new UpdateBuilder(true)
                .set("title", reviewDTO.getTitle())
                .set("content", reviewDTO.getContent())
                .set("rating", reviewDTO.getRating())
                .set("difficulty", reviewDTO.getDifficulty() != null ? reviewDTO.getDifficulty().toString() : null)
                .set("replayability", reviewDTO.getReplayable())
                .set("completed", reviewDTO.getCompleted())
                .set("platform", reviewDTO.getPlatform() != null ? reviewDTO.getPlatform().toString() : null)
                .set("gamelength", reviewDTO.getGameLength());
        updateBuilder.getParameters().add(id);
        return jdbcTemplate.update("UPDATE reviews " + updateBuilder.toQuery() + " WHERE id = ?", updateBuilder.getParameters().toArray());
    }

    @Override
    public Optional<Review> findById(Long id, Long activeUserId) {
        Optional<Review> review = jdbcTemplate.query("SELECT * FROM reviews " +
                "JOIN games as g ON g.id = reviews.gameid " +
                "JOIN users as u ON u.id = reviews.authorid "+
                "WHERE reviews.id = ?", CommonRowMappers.REVIEW_ROW_MAPPER, id)
                .stream()
                .findFirst();
       if (review.isPresent()) {
           if(activeUserId != null) {
               review.get().setFeedback(getReviewFeedback(id, activeUserId));
           }
           review.get().getAuthor().setPreferences(userDao.getPreferences(review.get().getAuthor().getId()));
           review.get().getReviewedGame().setGenres(gameDao.getGenresByGame(review.get().getReviewedGame().getId()));
        }
        return review;
    }

    private QueryBuilder getQueryBuilderFromFilter(ReviewFilter filter) {
        QueryBuilder queryBuilder = new QueryBuilder()
                .withList("gg.genreid", filter.getFilterGameGenres())
                .withList("ap.genreid", filter.getAuthorPreferences())
                .PARENTHESIS_OPEN()
                .withSimilar("r.content", filter.getReviewContent())
                .OR()
                .withSimilar("r.title", filter.getReviewContent())
                .PARENTHESIS_CLOSE()
                .AND()
                .withList("r.authorid", filter.getAuthors())
                .withExact("r.gameid", filter.getGameId())
                .withExact("r.completed", filter.getCompleted());
        if(filter.getMinTimePlayed() != null) {
            queryBuilder = queryBuilder.withGreaterOrEqual("COALESCE(r.gamelength, 0)", 3600 * filter.getMinTimePlayed());
        }
        if(filter.getPlatforms() != null) {
            queryBuilder = queryBuilder.withList("r.platform", filter.getPlatforms().stream().map(Platform::name).collect(Collectors.toList()));
        }
        if(filter.getDifficulties() != null) {
            queryBuilder = queryBuilder.withList("r.difficulty", filter.getDifficulties().stream().map(Difficulty::name).collect(Collectors.toList()));
        }
        return queryBuilder;
    }

    @Override
    public Paginated<Review> findAll(Page pagination, ReviewFilter filter, Ordering<ReviewOrderCriteria> ordering, Long activeUserId) {
        int totalPages = getPageCount(filter, pagination.getPageSize());
        if (pagination.getPageNumber() > totalPages || pagination.getPageNumber() <= 0) {
            return new Paginated<>(pagination.getPageNumber(), pagination.getPageSize(), totalPages, new ArrayList<>());
        }
        QueryBuilder query = getQueryBuilderFromFilter(filter);
        List<Object> preparedArgs = new ArrayList<>(query.toArguments());
        preparedArgs.add(pagination.getPageSize());
        preparedArgs.add(pagination.getOffset());
        List<Review> reviews = jdbcTemplate.query(
                "SELECT "+getTableColumnString()+", COALESCE(likes - dislikes, 0) AS net_likes, COALESCE(likes + dislikes, 0) AS controversial FROM " +
                        toTableString(filter)+
                        query.toQuery()+
                        toOrderString(ordering) +
                        " LIMIT ? OFFSET ?"
                , CommonRowMappers.REVIEW_ROW_MAPPER, preparedArgs.toArray());
        Map<Long, Set<Genre>> userPreferredGenres = new HashMap<>();
        Map<Long, List<Genre>> gameGenres = new HashMap<>();
        if( activeUserId != null) {
            Map<Long,ReviewFeedback> reviewFeedbacks = jdbcTemplate.query("SELECT * FROM reviewfeedback WHERE userid = ?", USER_REVIEW_FEEDBACK_MAPPER, activeUserId);
            for (Review review: reviews){
                review.setFeedback(reviewFeedbacks.get(review.getId()));
            }
        }
        for (Review r : reviews){
            if (!userPreferredGenres.containsKey(r.getAuthor().getId())) {
                userPreferredGenres.put(r.getAuthor().getId(), userDao.getPreferences(r.getAuthor().getId()));
            }
            if(!gameGenres.containsKey(r.getReviewedGame().getId())){
                gameGenres.put(r.getReviewedGame().getId(), gameDao.getGenresByGame(r.getReviewedGame().getId()));
            }
            r.getAuthor().setPreferences(userPreferredGenres.get(r.getAuthor().getId()));
            r.getReviewedGame().setGenres(gameGenres.get(r.getReviewedGame().getId()));
        }
        return new Paginated<>(pagination.getPageNumber(), pagination.getPageSize(), totalPages, reviews);
    }

    public List<Review> findAll(ReviewFilter filter, Ordering<ReviewOrderCriteria> ordering, Long activeUserId) {
        Page page = Page.with(1, count(filter).intValue());
        if(page.getPageSize() <=0){
            return new ArrayList<>();
        }
        return findAll(page, filter, ordering, activeUserId).getList();
    }

    private String getTableColumnString(){
        return "distinct r.id, r.authorid, r.gameid, r.title, r.content, r.createddate, r.rating, r.difficulty, r.gamelength, r.platform, r.completed, r.replayability,r.likes,r.dislikes," +
                " u.id, u.email,u.username, g.name, g.description, g.imageid,g.publisher, g.developer, g.publishdate, g.ratingsum, g.reviewcount";
    }
    @Override
    public Long count(ReviewFilter filter) {
        QueryBuilder query = getQueryBuilderFromFilter(filter);
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
        if (order == null || order.getOrderCriteria() == null) {
            return "";
        }
        return " ORDER BY " +
                order.getOrderCriteria().getAltName() +
                " " +
                ( order.getOrderDirection() == null ? "" : order.getOrderDirection().getAltName());
    }
}
