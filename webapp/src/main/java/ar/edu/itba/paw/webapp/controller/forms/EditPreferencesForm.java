package ar.edu.itba.paw.webapp.controller.forms;

import ar.edu.itba.paw.webapp.controller.annotations.ExistentGenreList;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@NotNull(message = "error.body.empty")
public class EditPreferencesForm {
    @ExistentGenreList
    @NotNull(message = "NotNull.editPreferencesForm.genres")
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
