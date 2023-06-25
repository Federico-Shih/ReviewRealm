package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.GameFilter;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.helpers.QueryBuilder;
import ar.edu.itba.paw.persistenceinterfaces.GameDao;
import ar.edu.itba.paw.persistenceinterfaces.PaginationDao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class GameHibernateDao implements GameDao, PaginationDao<GameFilter> {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Game create(String name, String description, String developer, String publisher, String imageid, List<Genre> genres, LocalDate publishDate, boolean suggested, User suggestedBy) {
        final Image image = em.getReference(Image.class, imageid);
        final Game game = new Game(name, description, developer, publisher, image, genres, publishDate, suggested, suggestedBy);
        em.persist(game);
        return game;
    }

    @Override
    public Optional<Game> edit(long gameId,String name, String description, String developer, String publisher, String imageid, List<Genre> genres, LocalDate publishDate) {

        final Game game = em.find(Game.class, gameId);
        if(game==null)
            return Optional.empty();
        game.setName(name);
        game.setDescription(description);
        game.setDeveloper(developer);
        game.setPublisher(publisher);
        if(imageid!=null) {
            final Image image = em.find(Image.class, imageid);
            game.setImage(image);
        }
        game.setGenres(genres);
        game.setPublishDate(publishDate);
        em.persist(game);
        return Optional.of(game);
    }

    @Override
    public Optional<Game> getById(long id) {
        final Game game = em.find(Game.class, id);
        return Optional.ofNullable(game);
    }

    @Override
    public Paginated<Game> findAll(Page page, GameFilter filter, Ordering<GameOrderCriteria> ordering) {
        int totalPages = getPageCount(filter, page.getPageSize());
        if (page.getPageNumber() > totalPages || page.getPageNumber() <= 0) {
            return new Paginated<>(page.getPageNumber(), page.getPageSize(), totalPages, new ArrayList<>());
        }
        QueryBuilder queryBuilder = getQueryBuilderFromFilter(filter);
        Query nativeQuery = em.createNativeQuery(
                "SELECT gameid FROM ("+
                        "SELECT distinct g.id as gameid, g.publishdate, g.name, g.ratingsum / coalesce(nullif(g.reviewcount, 0), 1) as averageRating  FROM " +
                        toTableString(filter) +
                        queryBuilder.toQuery() +
                        ") as games" +
                        toOrderString(ordering));
        prepareParametersForNativeQuery(queryBuilder, nativeQuery);
        nativeQuery.setMaxResults(page.getPageSize());
        nativeQuery.setFirstResult(page.getOffset().intValue());

        @SuppressWarnings("unchecked")
        final List<Long> idlist = (List<Long>) nativeQuery.getResultList().stream().map(n -> ((Number) n).longValue()).collect(Collectors.toList());

        final TypedQuery<Game> query = em.createQuery("from Game WHERE id IN :ids " + toOrderString(ordering), Game.class);
        query.setParameter("ids", idlist);
        return new Paginated<>(page.getPageNumber(), page.getPageSize(), totalPages, query.getResultList());
    }

    @Override
    public List<Genre> getGenresByGame(long id) {
        final Game game = em.find(Game.class, id);
        if(game == null) {
            return new ArrayList<>();
        }
        return game.getGenres();
    }

    @Override
    public Optional<List<Game>> getFavoriteGamesFromUser(long userId) {
        final User user = em.find(User.class, userId);
        if(user == null) {
            return Optional.empty();
        }
        return Optional.of(user.getFavoriteGames());
    }


    @Override
    public List<Game> getFavoriteGamesCandidates(long userId, int minRating) {
        final TypedQuery<Review> query = em.createQuery("select r from User u join Review r on u.id = r.author.id where r.rating >= :rating and u.id = :userId", Review.class);
        query.setParameter("rating", minRating);
        query.setParameter("userId", userId);

        return query.getResultList().stream().map(Review::getReviewedGame).collect(Collectors.toList());
    }

    @Override
    public boolean deleteFavoriteGameForUser(long userId, long gameId) {
        final User user = em.find(User.class, userId);
        if(user==null) return false;
        return user.getFavoriteGames().removeIf(game -> game.getId() == gameId);
    }

    @Override
    public List<Game> getRecommendationsForUser(List<Integer> userPreferences, List<Long> gamesToExclude) {
        final TypedQuery<Game> query = em.createQuery("select distinct g from Game g inner join g.genres genre " +
                "where " + (gamesToExclude.size() > 0 ? "g.id not in :exclude and " : "") + (userPreferences.size() > 0 ? "genre in :preferences" : "1 = 2") +
                " order by g.averageRating desc", Game.class);
        if (gamesToExclude.size() > 0) query.setParameter("exclude", gamesToExclude);
        if (userPreferences.size() > 0)
            query.setParameter("preferences", userPreferences.stream().map(Genre::valueFrom).collect(Collectors.toList()));
        return query.getResultList();
    }

    @Override
    public Optional<Set<Game>> getGamesReviewedByUser(long userId) {
        User user = em.find(User.class, userId);
        if (user == null) return Optional.empty();
        return Optional.of(user.getReviews().stream().map(Review::getReviewedGame).collect(Collectors.toSet()));
    }

    @Override
    public Optional<Game> addNewReview(long gameId, int rating) {
        final Optional<Game> game = Optional.ofNullable(em.find(Game.class, gameId));
        game.ifPresent((g) -> {
            g.setReviewCount(g.getReviewCount() + 1);
            g.setRatingSum(g.getRatingSum() + rating);
        });
        return game;
    }

    @Override
    public Optional<Game> modifyReview(long gameId, int oldRating, int newRating) {
        final Optional<Game> game = Optional.ofNullable(em.find(Game.class, gameId));
        game.ifPresent((g) -> {
            g.setRatingSum(g.getRatingSum() + newRating - oldRating);
        });
        return game;
    }

    @Override
    public Optional<Game> deleteReviewFromGame(long gameId, int rating) {
        final Optional<Game> game = Optional.ofNullable(em.find(Game.class, gameId));
        game.ifPresent((g) -> {
            g.setReviewCount(g.getReviewCount() - 1);
            g.setRatingSum(g.getRatingSum() - rating);
        });
        return game;
    }

    @Override
    public Optional<Game> setSuggestedFalse(long gameId) {
        final Optional<Game> game = Optional.ofNullable(em.find(Game.class, gameId));
        if(game.isPresent()) {
            game.get().setSuggestion(false);
            em.persist(game.get());
        }
        return game;
    }

    @Override
    public boolean deleteGame(long gameId) {
        final Game game = em.find(Game.class, gameId);
        if(game != null)
            em.remove(game);
        return game!=null;
    }

    @Override
    public Optional<User> replaceAllFavoriteGames(long userId, List<Long> gameIds) {
        User user = em.find(User.class, userId);
        if (user == null) return Optional.empty();
        List<Game> favGames = user.getFavoriteGames();
        favGames.clear();
        for (Long gameId : gameIds) {
            favGames.add(em.find(Game.class, gameId));
        }
        return Optional.of(user);
    }


    private QueryBuilder getQueryBuilderFromFilter(GameFilter filter) {
        return new QueryBuilder()
                .withSimilar("g.name", filter.getGameContent())
                .withList("gg.genreid", filter.getGameGenres())
                .withExact("g.publisher", filter.getPublisher())
                .withExact("g.suggestion", filter.getSuggested())
                .NOT().withList("g.id", filter.getGamesToExclude())
                .PARENTHESIS_OPEN()
                .withGreaterOrEqual("CASE WHEN g.reviewcount = 0 THEN -1 ELSE (g.ratingsum/g.reviewcount) END", filter.getMinRating())
                .withLessOrEqual("CASE WHEN g.reviewcount = 0 THEN -1 ELSE (g.ratingsum/g.reviewcount) END", filter.getMaxRating())
                .OR()
                .withLess("g.reviewcount", filter.getIncludeNoRating() ? 1 : 0)
                .PARENTHESIS_CLOSE()
                .AND()
                .withExact("g.developer", filter.getDeveloper());
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
        if (order == null || order.getOrderCriteria() == null) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        str.append(" ORDER BY ");
        str.append(order.getOrderCriteria().getAltName());
        if (order.getOrderDirection() != null) {
            str.append(" ");
            str.append(order.getOrderDirection().getAltName());
        }
        str.append(" NULLS LAST ");
        return str.toString();
    }

    @Override
    public Long count(GameFilter filter) {
        QueryBuilder queryBuilder = getQueryBuilderFromFilter(filter);
        Query nativeQuery = em.createNativeQuery("SELECT count(distinct g.id) FROM " + toTableString(filter) + queryBuilder.toQuery());
        prepareParametersForNativeQuery(queryBuilder, nativeQuery);

        return ((Number) nativeQuery.getSingleResult()).longValue();
    }

    private void prepareParametersForNativeQuery(QueryBuilder queryBuilder, Query nativeQuery) {
        int length = queryBuilder.toArguments().size();
        ArrayList<Object> array = new ArrayList<>(queryBuilder.toArguments());
        for (int i = 0; i < length; i += 1) {
            nativeQuery.setParameter(i + 1, array.get(i));
        }
    }
}
