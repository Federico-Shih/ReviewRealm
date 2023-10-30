package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class ReviewResponse extends BaseResponse {

    private static String BASE_PATH = "/reviews";
    private long id;
    private String title;
    private String content;
    private int rating;
    private long authorId;
    private long gameId;
    private Difficulty difficulty;
    private Double gameLength;
    private Platform platform;
    private Long likes;
    private Long dislikes;
    private Boolean completed;
    private Boolean replayable;



    public static ReviewResponse fromEntity(final UriInfo uri, Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setTitle(review.getTitle());
        response.setContent(review.getContent());
        response.setAuthorId(review.getAuthor().getId());
        response.setGameId(review.getReviewedGame().getId());
        response.setRating(review.getRating());
        response.setDifficulty(review.getDifficulty());
        response.setGameLength(review.getGameLength()); // Quizás debamos tener en cuenta la unidad de tiempo
        response.setPlatform(review.getPlatform());
        response.setLikes(review.getLikes());
        response.setDislikes(review.getDislikes());
        response.setCompleted(review.getCompleted());
        response.setReplayable(review.getReplayability());

        response.link("self", getLinkFromEntity(uri,review));
        response.link("game", uri.getBaseUriBuilder().path("games").path(String.valueOf(review.getReviewedGame().getId())).build());
        response.link("author", uri.getBaseUriBuilder().path("users").path(String.valueOf(review.getAuthor().getId())).build());
        response.link("feedback", uri.getBaseUriBuilder().path("reviews").path(String.valueOf(review.getId())).path("feedback").build());
        return response;
    }

    public static URI getLinkFromEntity(final UriInfo uri, Review review) {
        return uri.getBaseUriBuilder().path(BASE_PATH).path(String.valueOf(review.getId())).build();
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setGameLength(Double gameLength) {
        this.gameLength = gameLength;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public void setReplayable(Boolean replayable) {
        this.replayable = replayable;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public void setDislikes(Long dislikes) {
        this.dislikes = dislikes;
    }

    public Long getDislikes() {
        return dislikes;
    }

    public Long getLikes() {
        return likes;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getRating() {
        return rating;
    }

    public long getAuthorId() {
        return authorId;
    }

    public long getGameId() {
        return gameId;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Double getGameLength() {
        return gameLength;
    }

    public Platform getPlatform() {
        return platform;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public Boolean getReplayable() {
        return replayable;
    }
}
