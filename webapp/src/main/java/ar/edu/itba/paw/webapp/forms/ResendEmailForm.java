package ar.edu.itba.paw.webapp.forms;

import ar.edu.itba.paw.webapp.annotations.ExistentEmail;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ResendEmailForm {
    @ExistentEmail
    @Pattern(regexp = "^[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*(\\+[1-9][0-9]*)?@[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*$")
    @Size(max = 100)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
