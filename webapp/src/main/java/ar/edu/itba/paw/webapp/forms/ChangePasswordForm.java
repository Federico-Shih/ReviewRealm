package ar.edu.itba.paw.webapp.forms;

import ar.edu.itba.paw.webapp.annotations.ExistentEmail;
import ar.edu.itba.paw.webapp.annotations.FieldMatch;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ChangePasswordForm {
    @NotNull
    private String token;

    @NotNull
    @Size(min = 8, max = 100)
    private String password;


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
