package ar.edu.itba.paw.exceptions;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String message) {
        super(message);
    }
    public GameNotFoundException() {
        super();
    }
}
