package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.AuthenticationNeededException;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.responses.ExceptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.Response;

@Component
@Provider
public class AuthenticationNeededExceptionMapper implements ExceptionMapper<AuthenticationNeededException> {

    @Autowired
    private MessageSource messageSource;

     @Override
     public Response toResponse(AuthenticationNeededException e) {
         return Response.status(Response.Status.UNAUTHORIZED).entity(ExceptionResponse.of(messageSource.getMessage("authentication.needed", null,
                 LocaleHelper.getLocale()))).build();
     }

}
