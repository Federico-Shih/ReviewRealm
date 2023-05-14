package ar.edu.itba.paw.exceptions;

public class NoSuchGameException extends RuntimeException {
    public NoSuchGameException(String message) {
        super(message);
    }
}
