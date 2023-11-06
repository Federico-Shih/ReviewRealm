package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.ReportNotFoundException;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.responses.ExceptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Component
@Provider
public class ReportNotFoundExceptionMapper implements ExceptionMapper<ReportNotFoundException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(ReportNotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).entity(ExceptionResponse.of(messageSource.getMessage("report.not.found", null,
                LocaleHelper.getLocale()))).build();
    }
}
