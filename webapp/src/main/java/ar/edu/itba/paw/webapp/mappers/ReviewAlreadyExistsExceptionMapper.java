package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.ReviewAlreadyExistsException;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class ReviewAlreadyExistsExceptionMapper implements ExceptionMapper<ReviewAlreadyExistsException> {

    @Autowired
    private MessageSource messageSource;
    @Override
    public Response toResponse(ReviewAlreadyExistsException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity(messageSource.getMessage("review.already.exists", new Object[]{e.getReviewedGame().getName()},
                LocaleHelper.getLocale())).build();
    }
}
