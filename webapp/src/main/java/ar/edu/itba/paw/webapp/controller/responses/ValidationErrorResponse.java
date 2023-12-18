package ar.edu.itba.paw.webapp.controller.responses;

import javax.validation.ConstraintViolation;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

public class ValidationErrorResponse {
    private String message;
    private Object property;
    private Object value;

    public static ValidationErrorResponse fromValidationException(ConstraintViolation<?> violation) {
        ValidationErrorResponse response = new ValidationErrorResponse();
        response.message = violation.getMessage();
        response.property = StreamSupport.stream(() -> violation.getPropertyPath().spliterator(), Spliterator.ORDERED, false)
                .reduce((first, second) -> second).orElseThrow(RuntimeException::new).getName();
        response.value = violation.getInvalidValue();
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

    public Object getValue() {
        return value;
    }
}
