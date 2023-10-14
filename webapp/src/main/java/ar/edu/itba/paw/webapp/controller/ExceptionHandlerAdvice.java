package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.UserAlreadyEnabled;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import ar.edu.itba.paw.webapp.exceptions.CustomRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    private final UserService us;

    @Autowired
    public ExceptionHandlerAdvice(UserService us) {
        this.us = us;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @Produces(MediaType.APPLICATION_JSON)
    public Response handleHttpMessageNotReadableException() {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @ExceptionHandler(UserAlreadyEnabled.class)
    public Response handleUserAlreadyEnabled() {
        throw new CustomRuntimeException(Response.Status.BAD_REQUEST, "user.already.exists");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Response handleBadCredentialsException() {
        throw new CustomRuntimeException(Response.Status.UNAUTHORIZED, "authenticationexception");
    }

//    @ExceptionHandler(ObjectNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ModelAndView notFoundException() {
//        User loggedUser = AuthenticationHelper.getLoggedUser(us);
//        ModelAndView mav = new ModelAndView("errors/not-found");
//        mav.addObject("loggedUser", loggedUser);
//        return mav;
//    }
//
//    @ExceptionHandler(AccessDeniedException.class)
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    public ModelAndView accessDeniedException() {
//        User loggedUser = AuthenticationHelper.getLoggedUser(us);
//        ModelAndView mav = new ModelAndView("errors/prohibited");
//        mav.addObject("loggedUser", loggedUser);
//        return mav;
//    }
//
//    @ExceptionHandler(NoHandlerFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ModelAndView generic404() {
//        User loggedUser = AuthenticationHelper.getLoggedUser(us);
//        ModelAndView mav = new ModelAndView("errors/not-found");
//        mav.addObject("loggedUser", loggedUser);
//        return mav;
//    }
//
//    @ExceptionHandler(MaxUploadSizeExceededException.class)
//    public ModelAndView handleMaxSizeException() {
//        return new ModelAndView("errors/file-too-large");
//    }
}
