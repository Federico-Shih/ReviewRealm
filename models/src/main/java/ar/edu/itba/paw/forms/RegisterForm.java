package ar.edu.itba.paw.forms;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class RegisterForm {

    @Size(min=4, max = 24)
    private String username;

    @Pattern(regexp = "^[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*(\\+[1-9][0-9]*)?@[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*$")
    @Size(max = 100)
    private String email;

    @Size(min = 8, max = 100)
    private String password;

    @Size(min = 8, max = 100)
    private String repeatPassword;

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

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
