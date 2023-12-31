package ar.edu.itba.paw.dtos.filtering;

import java.util.List;

// To be used between Services and Persistence
public class GameFilter {
    private final List<Integer> gameGenres;
    private final String gameContent;
    private final String developer;
    private final String publisher;
    private final Float minRating;
    private final Float maxRating;
    private final boolean includeNoRating;
    private final Boolean isSuggested;
    private final List<Long> gamesToExclude;
    private final Long favoriteGamesOf;
    private final Long recommendedFor;
    private final Long notReviewedBy;
    private final Boolean deleted;

    public GameFilter(List<Integer> gameGenres, String gameContent, String developer, String publisher, Float minRating, Float maxRating, boolean includeNoRating, Boolean isSuggested, List<Long> gamesToExclude, Long favoriteGamesOf, Long recommendedFor, Long notReviewedBy, Boolean deleted) {
        this.gameGenres = gameGenres;
        this.gameContent = gameContent;
        this.developer = developer;
        this.publisher = publisher;
        this.minRating = minRating;
        this.maxRating = maxRating;
        this.includeNoRating = includeNoRating;
        this.isSuggested = isSuggested;
        this.gamesToExclude = gamesToExclude;
        this.favoriteGamesOf = favoriteGamesOf;
        this.recommendedFor = recommendedFor;
        this.notReviewedBy = notReviewedBy;
        this.deleted = deleted;
    }

    public List<Integer> getGameGenres() {
        return gameGenres;
    }

    public String getGameContent() {
        return gameContent;
    }

    public String getDeveloper() {
        return developer;
    }

    public String getPublisher() {
        return publisher;
    }

    public Float getMinRating() {
        return minRating;
    }

    public Float getMaxRating() {
        return maxRating;
    }

    public boolean getIncludeNoRating() { return includeNoRating; }

    public Boolean getSuggested() { return isSuggested; }

    public List<Long> getGamesToExclude() {
        return gamesToExclude;
    }

    public Long getFavoriteGamesOf() {
        return favoriteGamesOf;
    }

    public Long getRecommendedFor() {
        return recommendedFor;
    }

    public Long getNotReviewedBy() {
        return notReviewedBy;
    }

    public boolean isProperRecommendedFor(){
        return gameContent == null && (gameGenres == null || gameGenres.isEmpty()) && (minRating == null  || Float.compare(minRating,1.0f)==0) && (maxRating ==null || Float.compare(maxRating,10.0f)==0)  && includeNoRating && !isSuggested && favoriteGamesOf == null && notReviewedBy == null;
    }
    public boolean isProperFavoriteOf(){
        return gameContent == null && (gameGenres == null || gameGenres.isEmpty()) &&  (minRating == null  || Float.compare(minRating,1.0f)==0) && (maxRating ==null || Float.compare(maxRating,10.0f)==0) && includeNoRating && !isSuggested && recommendedFor == null && notReviewedBy == null;
    }

    public boolean isProperNotReviewedBy() {
        return (gameGenres == null || gameGenres.isEmpty()) &&  (minRating == null  || Float.compare(minRating,1.0f)==0) && (maxRating ==null || Float.compare(maxRating,10.0f)==0) && includeNoRating && !isSuggested && favoriteGamesOf == null && recommendedFor == null;
    }

    public Boolean getDeleted() {
        return deleted;
    }
}
