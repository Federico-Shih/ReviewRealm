package ar.edu.itba.paw.webapp.controller.forms;

import ar.edu.itba.paw.webapp.controller.annotations.ExistentGameId;

import javax.validation.constraints.NotNull;

@NotNull(message = "error.body.empty")
public class FavoriteGameForm {
    @ExistentGameId
    private long gameId;

    public FavoriteGameForm() {
    }

    public FavoriteGameForm(long gameId) {
        this.gameId = gameId;
    }

    public long getGameId() {
        return gameId;
    }
}
