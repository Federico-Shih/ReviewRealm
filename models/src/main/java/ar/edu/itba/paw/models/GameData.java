package ar.edu.itba.paw.models;

public class GameData{
    private final Game game;
    private final Double averageScore;

    public GameData(Game game, Double averageScore) {
        this.game = game;
        this.averageScore = averageScore;
    }

    public Game getGame() {
        return game;
    }

    public Double getAverageScore() {
        return averageScore;
    }
}
