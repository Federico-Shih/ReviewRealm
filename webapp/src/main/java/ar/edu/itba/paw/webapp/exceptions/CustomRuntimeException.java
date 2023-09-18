package ar.edu.itba.paw.webapp.exceptions;

import org.springframework.http.HttpStatus;

import javax.ws.rs.core.Response;

public class CustomRuntimeException extends RuntimeException {
    private final Response.StatusType code;
    private final String messageCode;

    // MessageCode tiene que ser codigo i18n valido
    public CustomRuntimeException(Response.StatusType code, String messageCode) {
        super(messageCode);
        this.code = code;
        this.messageCode = messageCode;
    }

    public Response.StatusType getCode() {
        return code;
    }

    public String getMessageCode() {
        return messageCode;
    }
}
