package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.ReviewAlreadyExistsException;
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
public class ReviewAlreadyExistsExceptionMapper implements ExceptionMapper<ReviewAlreadyExistsException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewAlreadyExistsExceptionMapper.class);

    @Autowired
    private MessageSource messageSource;
    @Override
    public Response toResponse(ReviewAlreadyExistsException e) {
        LOGGER.error("{}: {}", e.getClass().getName(), messageSource.getMessage("review.already.exists", new Object[]{e.getReviewedGame().getName()},
                Locale.ENGLISH));
        return Response.status(Response.Status.CONFLICT).type(MediaType.APPLICATION_JSON).entity(ExceptionResponse.of(messageSource.getMessage("review.already.exists", new Object[]{e.getReviewedGame().getName()},
                LocaleHelper.getLocale()))).build();
    }
}
