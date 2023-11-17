package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.ReportAlreadyResolvedException;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.responses.ExceptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class ReportAlreadyResolvedExceptionMapper implements ExceptionMapper<ReportAlreadyResolvedException> {

    @Autowired
    private MessageSource messageSource;
    @Override
    public Response toResponse(ReportAlreadyResolvedException userAlreadyEnabled) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(ExceptionResponse.of(messageSource.getMessage("report.already.resolved", null, LocaleHelper.getLocale())))
                .build();
    }
}
