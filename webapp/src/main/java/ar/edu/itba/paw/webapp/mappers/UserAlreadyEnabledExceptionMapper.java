package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.UserAlreadyEnabled;
import ar.edu.itba.paw.webapp.exceptions.CustomRuntimeException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class UserAlreadyEnabledExceptionMapper implements ExceptionMapper<UserAlreadyEnabled> {
    @Override
    public Response toResponse(UserAlreadyEnabled userAlreadyEnabled) {
        throw new CustomRuntimeException(Response.Status.BAD_REQUEST, "user.already.exists");
    }
}
