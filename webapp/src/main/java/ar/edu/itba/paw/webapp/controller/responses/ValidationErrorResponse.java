package ar.edu.itba.paw.webapp.controller.responses;

import javax.validation.ConstraintViolation;

public class ValidationErrorResponse {
    private String message;
    private Object property;

    public static ValidationErrorResponse fromValidationException(ConstraintViolation<?> violation) {
        ValidationErrorResponse response = new ValidationErrorResponse();
        response.message = violation.getMessage();
        response.property = violation.getInvalidValue();
        return response;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getProperty() {
        return property;
    }
}
