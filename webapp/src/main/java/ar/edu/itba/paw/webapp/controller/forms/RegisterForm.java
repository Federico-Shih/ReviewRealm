package ar.edu.itba.paw.webapp.controller.forms;

import ar.edu.itba.paw.webapp.controller.annotations.FieldMatch;
import ar.edu.itba.paw.webapp.controller.annotations.UniqueEmail;
import ar.edu.itba.paw.webapp.controller.annotations.UniqueUsername;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class RegisterForm {

    @UniqueUsername(message = "UniqueUsername.registerForm.username")
    @Size(min=4, max = 24, message = "Size.registerForm.username")
    private String username;

    @UniqueEmail(message = "UniqueEmail.registerForm.email")
    @Pattern(regexp = "^[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*(\\+[1-9][0-9]*)?@[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*$", message = "Pattern.registerForm.email")
    @Size(max = 100, message = "Size.registerForm.email")
    private String email;

    @Size(min = 8, max = 100, message = "Size.registerForm.password")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
