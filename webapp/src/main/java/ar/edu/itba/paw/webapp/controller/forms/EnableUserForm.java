package ar.edu.itba.paw.webapp.controller.forms;

import javax.validation.constraints.NotNull;

public class EnableUserForm {
    @NotNull(message = "NotNull.property")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
