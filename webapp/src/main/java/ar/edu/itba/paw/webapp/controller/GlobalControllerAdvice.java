package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {
    private final UserService us;
    @Autowired
    public GlobalControllerAdvice(UserService us) {
        this.us = us;
    }

    @ModelAttribute("loggedUser")
    public User loggedUser() {
        return AuthenticationHelper.getLoggedUser(us);
    }

    @ModelAttribute("isModerator")
    public boolean isModerator() {
        return AuthenticationHelper.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MODERATOR"));
    }
}
