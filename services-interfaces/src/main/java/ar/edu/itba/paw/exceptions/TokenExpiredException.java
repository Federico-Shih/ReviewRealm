package ar.edu.itba.paw.exceptions;

public class TokenExpiredException extends Exception {
    public TokenExpiredException(String s) {
        super(s);
    }
}
