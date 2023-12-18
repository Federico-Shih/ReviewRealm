package ar.edu.itba.paw.webapp.controller.forms;

import ar.edu.itba.paw.webapp.controller.annotations.ExistentUserId;

import javax.validation.constraints.NotNull;

public class FollowingForm {
    @ExistentUserId
    @NotNull(message = "NotNull.property")
    private Long userId;

    public Long getUserId() {
        return userId;
    }
}
