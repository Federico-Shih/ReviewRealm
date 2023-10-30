package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.exceptions.CustomRuntimeException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class UnrecognizedPropertyExceptionMapper  implements ExceptionMapper<UnrecognizedPropertyException> {

    @Autowired
    private MessageSource messageSource;
    @Override
    public Response toResponse(UnrecognizedPropertyException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity(messageSource.getMessage("property.unrecognized", new Object[]{e.getPropertyName()},
                null)).build();
    }
}
