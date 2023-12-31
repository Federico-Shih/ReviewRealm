package ar.edu.itba.paw.webapp.controller.forms;

import javax.validation.constraints.NotNull;

public class PatchGameForm {
    @NotNull(message = "NotNull.property")
    private Boolean accept;

    public Boolean getAccept() {
        return accept;
    }

    public void setEnabled(Boolean enabled) {
        this.accept = enabled;
    }
}
