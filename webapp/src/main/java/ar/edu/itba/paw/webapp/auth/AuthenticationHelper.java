package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class AuthenticationHelper {
    private static Optional<PawAuthUserDetails> getPrincipalFromContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser"))
            return Optional.empty();
        return Optional.of((PawAuthUserDetails) auth.getPrincipal());
    }
    public static User getLoggedUser(UserService us) {
        Optional<PawAuthUserDetails> principal = getPrincipalFromContext();
        return principal.flatMap(pawAuthUserDetails -> us.getUserByEmail(pawAuthUserDetails.getUsername())).orElse(null);
    }

    public static Collection<? extends GrantedAuthority> getAuthorities() {
        Optional<PawAuthUserDetails> principal = getPrincipalFromContext();
        if (!principal.isPresent()) return new ArrayList<>();
        return principal.get().getAuthorities();
    }
}
