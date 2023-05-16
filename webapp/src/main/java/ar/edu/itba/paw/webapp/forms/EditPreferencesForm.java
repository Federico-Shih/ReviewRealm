package ar.edu.itba.paw.webapp.forms;


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
