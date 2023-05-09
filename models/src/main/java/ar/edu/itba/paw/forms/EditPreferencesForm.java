package ar.edu.itba.paw.forms;

import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.Game;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class EditPreferencesForm {

    private List<Integer> genres;

    public List<Integer> getGenres() {
        if(genres == null)
            return new ArrayList<>();
        return genres;
    }

    public void setGenres(List<Integer> genres) {
        this.genres = genres;
    }
}
