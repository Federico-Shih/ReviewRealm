package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.GameReviewData;
import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDate;

public class GameResponse extends BaseResponse {

    public static final String BASE_PATH = "/games";

    private long id;

    private String name;

    private String description;

    private String developer;

    private String publisher;

    private LocalDate publishDate;

    private int ratingSum;

    private int reviewCount;

    //GameReviewData
    private Difficulty averageDifficulty = null;
    private Platform averagePlatform = null;
    private double averageGameTime = -1;
    private double replayability = -1;
    private double completability = -1;

    //Link reviews,Image,self

    public static GameResponse fromEntity(final UriInfo uri, Game game, GameReviewData gameReviewData, User user){
        GameResponse response = new GameResponse();

        response.setId(game.getId());
        response.setName(game.getName());
        response.setDescription(game.getDescription());
        response.setDeveloper(game.getDeveloper());
        response.setPublisher(game.getPublisher());
        response.setPublishDate(game.getPublishDate());
        response.setRatingSum(game.getRatingSum());
        response.setReviewCount(game.getReviewCount());
        if(gameReviewData != null) {
            response.setAverageDifficulty(gameReviewData.getAverageDifficulty());
            response.setAveragePlatform(gameReviewData.getAveragePlatform());
            response.setAverageGameTime(gameReviewData.getAverageGameTime());
            response.setReplayability(gameReviewData.getReplayability());
            response.setCompletability(gameReviewData.getCompletability());
        }



        response.link("self",uri.getBaseUriBuilder().path(BASE_PATH).path(String.valueOf(game.getId())).build());
        //Todo: check with review paths
        response.link("reviews",uri.getBaseUriBuilder().path("reviews").queryParam("gameId",game.getId()).build());
        if(user != null) {
            response.link("userReview", uri.getBaseUriBuilder().path("reviews").queryParam("authorId", user.getId()).queryParam("gameId", game.getId()).build());
        }
        response.link("image",uri.getBaseUriBuilder().path("images").path(game.getImage().getId()).build());
        response.link("genres",uri.getBaseUriBuilder().path("games").path(String.valueOf(game.getId())).path("genres").build());


        return response;
    }
    public static URI getLinkFromEntity(final UriInfo uri, Game game) {
        return uri.getBaseUriBuilder().path(BASE_PATH).path(String.valueOf(game.getId())).build();
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }


    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public void setRatingSum(int ratingSum) {
        this.ratingSum = ratingSum;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public long getId() {
        return id;
    }

    public void setAverageDifficulty(Difficulty averageDifficulty) {
        this.averageDifficulty = averageDifficulty;
    }

    public void setAveragePlatform(Platform averagePlatform) {
        this.averagePlatform = averagePlatform;
    }

    public void setAverageGameTime(double averageGameTime) {
        this.averageGameTime = averageGameTime;
    }

    public void setReplayability(double replayability) {
        this.replayability = replayability;
    }

    public void setCompletability(double completability) {
        this.completability = completability;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDeveloper() {
        return developer;
    }

    public String getPublisher() {
        return publisher;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public int getRatingSum() {
        return ratingSum;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public Difficulty getAverageDifficulty() {
        return averageDifficulty;
    }

    public Platform getAveragePlatform() {
        return averagePlatform;
    }

    public double getAverageGameTime() {
        return averageGameTime;
    }

    public double getReplayability() {
        return replayability;
    }

    public double getCompletability() {
        return completability;
    }
}
