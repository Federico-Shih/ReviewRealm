package ar.edu.itba.paw.exceptions;

public class InvalidUserException extends Exception {
    private boolean emailAlreadyExists;
    private boolean usernameAlreadyExists;

    public InvalidUserException() {
        super();
        this.emailAlreadyExists = false;
        this.usernameAlreadyExists = false;
    }

    public InvalidUserException(boolean emailAlreadyExists, boolean usernameAlreadyExists) {
        super();
        this.emailAlreadyExists = emailAlreadyExists;
        this.usernameAlreadyExists = usernameAlreadyExists;
    }

    public boolean hasErrors() {
        return emailAlreadyExists || usernameAlreadyExists;
    }

    public void setEmailAlreadyExists() {
        this.emailAlreadyExists = true;
    }

    public void setUsernameAlreadyExists() {
        this.usernameAlreadyExists = true;
    }

    public boolean doesEmailAlreadyExist() {
        return emailAlreadyExists;
    }

    public boolean doesUsernameAlreadyExist() {
        return usernameAlreadyExists;
    }
}
