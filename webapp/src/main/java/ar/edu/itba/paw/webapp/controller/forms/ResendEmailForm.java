package ar.edu.itba.paw.webapp.controller.forms;

import ar.edu.itba.paw.webapp.controller.annotations.ExistentEmail;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ResendEmailForm {
    @ExistentEmail(message = "ExistentEmail.emailForm.email")
    @Pattern(regexp = "^[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*(\\+[1-9][0-9]*)?@[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*$", message = "Pattern.resendEmailForm.email")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
