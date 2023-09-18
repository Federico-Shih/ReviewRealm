package ar.edu.itba.paw.webapp.forms;

import ar.edu.itba.paw.webapp.annotations.ExistentUserId;

import javax.validation.constraints.NotNull;

public class FollowingForm {
    @ExistentUserId
    @NotNull
    private Long userId;

    public Long getUserId() {
        return userId;
    }
}
