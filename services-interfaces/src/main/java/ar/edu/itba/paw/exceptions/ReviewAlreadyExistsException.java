package ar.edu.itba.paw.exceptions;

import ar.edu.itba.paw.models.Game;

public class ReviewAlreadyExistsException extends RuntimeException {
    private final Game reviewedGame;

    public ReviewAlreadyExistsException(Game reviewedGame) {
        super();
        this.reviewedGame = reviewedGame;
    }

    public Game getReviewedGame() {
        return reviewedGame;
    }
}
