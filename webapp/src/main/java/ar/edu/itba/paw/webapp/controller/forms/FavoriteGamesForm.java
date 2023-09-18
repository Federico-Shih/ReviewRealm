package ar.edu.itba.paw.webapp.controller.forms;

import javax.validation.constraints.Size;
import java.util.List;

public class FavoriteGamesForm {
    @Size(max=3)
    private List<Long> gameIds;

    public List<Long> getGameIds() {
        return gameIds;
    }

    public void setGameIds(List<Long> gameIds) {
        this.gameIds = gameIds;
    }
}
