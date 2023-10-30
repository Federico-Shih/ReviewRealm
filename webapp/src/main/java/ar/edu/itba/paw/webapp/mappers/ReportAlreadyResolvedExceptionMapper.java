package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.ReportAlreadyResolvedException;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class ReportAlreadyResolvedExceptionMapper implements ExceptionMapper<ReportAlreadyResolvedException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(ReportAlreadyResolvedException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity(messageSource.getMessage("report.already.resolved", null,
                LocaleHelper.getLocale())).build();
    }
}
