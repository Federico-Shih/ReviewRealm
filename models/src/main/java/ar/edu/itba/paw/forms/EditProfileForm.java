package ar.edu.itba.paw.forms;

import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.Game;

import javax.validation.constraints.Size;
import java.util.List;

public class EditProfileForm {

    private List<Integer> genres;

    @Size(max=3)
    private List<Game> games;

    public List<Integer> getGenres() {
        return genres;
    }

    public void setGenres(List<Integer> genres) {
        this.genres = genres;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }
}
