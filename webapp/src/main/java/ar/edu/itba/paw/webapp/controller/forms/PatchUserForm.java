package ar.edu.itba.paw.webapp.controller.forms;

import ar.edu.itba.paw.webapp.controller.annotations.ExistentGenreList;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PatchUserForm {
    @Size(min = 8, max = 100, message = "Size.passwordForm.password")
    private String password;

    @ExistentGenreList(nullable = true)
    private List<Integer> genres;

    @Min(value = 1, message = "Min.avatarid")
    @Max(value = 6, message = "Max.avatarid")
    private Long avatarId;

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

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }
}
