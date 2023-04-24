package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuthenticationHelper {
    public static Optional<User> getLoggedUser(UserService us) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser"))
            return Optional.empty();
        final PawAuthUserDetails userDetails = (PawAuthUserDetails) auth.getPrincipal();
        return us.getUserByEmail(userDetails.getUsername());
    }
}
