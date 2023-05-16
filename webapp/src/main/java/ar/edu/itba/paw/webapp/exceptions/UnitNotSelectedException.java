package ar.edu.itba.paw.webapp.exceptions;

public class UnitNotSelectedException extends RuntimeException {
    public UnitNotSelectedException() {
        super("No unit found");
    }
}
