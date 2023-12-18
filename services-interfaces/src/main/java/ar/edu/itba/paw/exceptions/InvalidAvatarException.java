package ar.edu.itba.paw.exceptions;

public class InvalidAvatarException extends Exception {
    private final long value;
    public InvalidAvatarException(long value) {
        super(String.format("%d is not a valid avatar", value));
        this.value = value;
    }

    public long getValue() {
        return this.value;
    }
}
