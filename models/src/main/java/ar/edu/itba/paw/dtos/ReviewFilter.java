package ar.edu.itba.paw.dtos;


import java.util.List;

public class ReviewFilter {
    List<Integer> filterGameGenres;

    List<Integer> authorPreferences;

    List<Long> authors;

    String reviewContent;
    Integer gameId;

    public ReviewFilter(List<Integer> filterGameGenres, List<Integer> authorPreferences, List<Long> authors, String reviewContent, Integer gameId) {
        this.filterGameGenres = filterGameGenres;
        this.authorPreferences = authorPreferences;
        this.authors = authors;
        this.reviewContent = reviewContent;
        this.gameId = gameId;
    }

    public List<Integer> getFilterGameGenres() {
        return filterGameGenres;
    }

    public Integer getGameId() {
        return gameId;
    }

    public List<Integer> getAuthorPreferences() {
        return authorPreferences;
    }

    public List<Long> getAuthors() {
        return authors;
    }

    public String getReviewContent() {
        return reviewContent;
    }
}
