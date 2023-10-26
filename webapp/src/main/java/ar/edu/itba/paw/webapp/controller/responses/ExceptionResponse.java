package ar.edu.itba.paw.webapp.controller.responses;

public class ExceptionResponse {
    private final String message;

    public ExceptionResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static ExceptionResponse of(String message) {
        return new ExceptionResponse(message);
    }
}
