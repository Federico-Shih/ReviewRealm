package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.responses.ExceptionResponse;
import ar.edu.itba.paw.webapp.exceptions.UnitNotSelectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Component
public class UnitNotSelectedExceptionMapper implements ExceptionMapper<UnitNotSelectedException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(UnitNotSelectedException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity(ExceptionResponse.of(messageSource.getMessage("unit.exception",
                null, LocaleHelper.getLocale()))).build();
    }
}
