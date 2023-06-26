package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.exceptions.ObjectNotFoundException;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import java.nio.file.AccessDeniedException;

@Controller
public class ExceptionController {

    @Autowired
    public ExceptionController() {

    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @RequestMapping("errors/404")
    public ModelAndView notFoundException() {
        throw new ObjectNotFoundException();
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @RequestMapping("errors/403")
    public ModelAndView wrongAccessException() throws AccessDeniedException {
        throw new AccessDeniedException("access-denied");
    }
}
