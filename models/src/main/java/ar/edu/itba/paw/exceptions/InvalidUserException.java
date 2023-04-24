package ar.edu.itba.paw.exceptions;

public class InvalidUserException extends Exception {
    private boolean emailAlreadyExists;
    private boolean usernameAlreadyExists;
    private boolean unexpectedError;

    public InvalidUserException() {
        super();
        this.emailAlreadyExists = false;
        this.usernameAlreadyExists = false;
        this.unexpectedError = false;
    }

    public InvalidUserException(boolean emailAlreadyExists, boolean usernameAlreadyExists, boolean unexpectedError) {
        super();
        this.emailAlreadyExists = emailAlreadyExists;
        this.usernameAlreadyExists = usernameAlreadyExists;
        this.unexpectedError = unexpectedError;
    }

    public boolean hasErrors() {
        return emailAlreadyExists || usernameAlreadyExists || unexpectedError;
    }

    public void setEmailAlreadyExists() {
        this.emailAlreadyExists = true;
    }

    public void setUsernameAlreadyExists() {
        this.usernameAlreadyExists = true;
    }

    public void setUnexpectedError() {
        this.unexpectedError = true;
    }

    public boolean doesEmailAlreadyExist() {
        return emailAlreadyExists;
    }

    public boolean doesUsernameAlreadyExist() {
        return usernameAlreadyExists;
    }
}
