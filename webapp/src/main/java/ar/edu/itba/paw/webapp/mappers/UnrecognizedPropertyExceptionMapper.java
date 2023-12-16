package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.responses.ExceptionResponse;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
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
public class UnrecognizedPropertyExceptionMapper  implements ExceptionMapper<UnrecognizedPropertyException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnrecognizedPropertyExceptionMapper.class);
    @Autowired
    private MessageSource messageSource;
    @Override
    public Response toResponse(UnrecognizedPropertyException e) {
        LOGGER.error("{}: {}", e.getClass().getName(), messageSource.getMessage("property.unrecognized", new Object[]{e.getPropertyName()},
                Locale.ENGLISH));
        return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(ExceptionResponse.of(messageSource.getMessage("property.unrecognized", new Object[]{e.getPropertyName()},
                LocaleHelper.getLocale()))).build();
    }
}
