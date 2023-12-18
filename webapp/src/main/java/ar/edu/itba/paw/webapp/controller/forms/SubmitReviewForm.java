package ar.edu.itba.paw.webapp.controller.forms;

import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.GamelengthUnit;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentGameId;
import ar.edu.itba.paw.webapp.exceptions.UnitNotSelectedException;
import javax.validation.constraints.*;

public class SubmitReviewForm extends EditReviewForm{

    @ExistentGameId
    private long gameId;

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long authorId) {
        this.gameId = authorId;
    }

    public SubmitReviewForm() {
    }

}
