package ar.edu.itba.paw.exceptions;

public class InvalidAvatarException extends Exception {
    public InvalidAvatarException(long value) {super(String.format("%d is not a valid avatar", value));}
}
