package ar.edu.itba.paw.webapp.forms;

import javax.validation.constraints.Size;

public class ChangePasswordForm {
    @Size(min = 8, max = 100)
    private String password;

    @Size(min = 8, max = 100)
    private String repeatPassword;

    public String getPassword() {
        return password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public boolean passwordsMatch() {
        return password.equals(repeatPassword);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
