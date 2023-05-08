package ar.edu.itba.paw.exceptions;

public class TokenNotFoundException extends Exception {
    public TokenNotFoundException() {
        super();
    }

    public TokenNotFoundException(String message) {
        super(message);
    }
}
