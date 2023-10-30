package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.EmailAlreadyExistsException;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class EmailAlreadyExistsExceptionMapper implements ExceptionMapper<EmailAlreadyExistsException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(EmailAlreadyExistsException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity(messageSource.getMessage("email.already.exists",
                null, LocaleHelper.getLocale())).build();
    }
}
