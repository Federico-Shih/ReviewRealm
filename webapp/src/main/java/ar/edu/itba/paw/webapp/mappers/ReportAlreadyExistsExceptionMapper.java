package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.ReportAlreadyExistsException;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.responses.ExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Locale;

@Component
@Provider
public class ReportAlreadyExistsExceptionMapper implements ExceptionMapper<ReportAlreadyExistsException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportAlreadyExistsExceptionMapper.class);
    @Autowired
    private MessageSource messageSource;
    @Override
    public Response toResponse(ReportAlreadyExistsException e) {
        LOGGER.error("{} : {}", e.getClass().getName(), messageSource.getMessage("report.already.exists", null,
                Locale.ENGLISH));
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(ExceptionResponse.of(messageSource.getMessage("report.already.exists", null, LocaleHelper.getLocale())))
                .build();
    }
}
