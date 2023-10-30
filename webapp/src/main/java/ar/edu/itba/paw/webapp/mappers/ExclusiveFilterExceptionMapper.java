package ar.edu.itba.paw.webapp.mappers;


import ar.edu.itba.paw.exceptions.AuthenticationNeededException;
import ar.edu.itba.paw.exceptions.ExclusiveFilterException;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.exceptions.CustomRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class ExclusiveFilterExceptionMapper implements ExceptionMapper<ExclusiveFilterException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(ExclusiveFilterException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity(messageSource.getMessage(e.getMessage(), null,
                LocaleHelper.getLocale())).build();
    }
}
