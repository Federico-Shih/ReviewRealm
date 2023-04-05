package ar.edu.itba.paw.exceptions;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String objectName) {
        super(String.format("%s was not found", objectName));
    }
}
