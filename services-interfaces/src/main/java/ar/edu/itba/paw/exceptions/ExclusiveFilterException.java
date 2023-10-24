package ar.edu.itba.paw.exceptions;

public class ExclusiveFilterException extends RuntimeException{
    public ExclusiveFilterException() {
        super();
    }

    public ExclusiveFilterException(String message) {
        super(message);
    }
}
