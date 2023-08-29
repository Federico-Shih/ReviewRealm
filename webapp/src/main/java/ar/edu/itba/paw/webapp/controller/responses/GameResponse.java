package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.GameReviewData;

import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.util.List;

public class GameResponse extends BaseResponse {

    private static final String BASE_PATH = "/games";

    private long id;

    private String name;

    private String description;

    private String developer;

    private String publisher;

    private List<Genre> genres;

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

    public static GameResponse fromEntity(final UriInfo uri, Game game, GameReviewData gameReviewData){
        GameResponse response = new GameResponse();

        response.setId(game.getId());
        response.setName(game.getName());
        response.setDescription(game.getDescription());
        response.setDeveloper(game.getDeveloper());
        response.setPublisher(game.getPublisher());
        response.setGenres(game.getGenres());
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
        response.link("reviews",uri.getBaseUriBuilder().path("reviews").queryParam("gameId",game.getId()).build());
//        response.link("userReview",uri.getBaseUriBuilder().path("reviews").queryParam("authorId",game.getId()).build()); TODO: Conseguir el usuario logueado
        response.link("image",uri.getBaseUriBuilder().path("images").path(game.getImage().getId()).build());

        return response;
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

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
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
}
