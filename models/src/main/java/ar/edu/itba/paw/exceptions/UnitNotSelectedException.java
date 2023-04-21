package ar.edu.itba.paw.exceptions;

public class UnitNotSelectedException extends RuntimeException {
    public UnitNotSelectedException() {
        super("No unit found");
    }
}
