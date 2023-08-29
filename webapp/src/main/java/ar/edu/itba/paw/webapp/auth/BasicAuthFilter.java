package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.Base64;

@Component
public class BasicAuthFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && !header.isEmpty()) {
            if (header.startsWith("Basic")) {
                basicAuthentication(header, request, response);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void basicAuthentication(String header, HttpServletRequest request, HttpServletResponse response) {
        final String encodedCredentials = header.split(" ")[1];
        try {
            String[] credentials = new String(Base64.getDecoder().decode(encodedCredentials)).split(":");
            if (credentials.length != 2)
                return;

            String username = credentials[0].trim();
            String password = credentials[1].trim();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, password);

            Authentication authenticate = authenticationManager.authenticate(authentication);

            UserDetails user = (UserDetails) authenticate.getPrincipal();
            User fullUser = userService.getUserByEmail(user.getUsername()).orElse(null);
            if (fullUser == null)
                return;

            response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenUtil.generateAccessToken(fullUser));
            response.setHeader("X-Refresh", "Bearer " + jwtTokenUtil.generateRefreshToken(fullUser));

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (IllegalArgumentException ignored) {

        }
    }
}
