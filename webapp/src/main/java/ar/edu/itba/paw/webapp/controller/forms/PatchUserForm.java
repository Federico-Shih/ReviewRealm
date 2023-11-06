package ar.edu.itba.paw.webapp.controller.forms;

import ar.edu.itba.paw.webapp.controller.annotations.ExistentGenreList;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PatchUserForm {
    @Size(min = 8, max = 100, message = "Size.passwordForm.password")
    private String password;

    @AssertTrue(message = "AssertTrue.patchuserForm.enabled")
    private Boolean enabled;

    @ExistentGenreList(nullable = true)
    private List<Integer> genres;

    public Set<Integer> getGenres() {
        if(genres == null)
            return new HashSet<>();
        return new HashSet<>(genres);
    }

    public void setGenres(List<Integer> genres) {
        this.genres = genres;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
