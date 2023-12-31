package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.exceptions.TokenExpiredException;
import ar.edu.itba.paw.models.ExpirationToken;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class BasicAuthFilter extends OncePerRequestFilter {
    private static final String REFRESH_HEADER = "X-Refresh";

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
                boolean skipBasic = basicTokenAuthentication(header, request, response);
                if (!skipBasic) {
                    basicAuthentication(header, request, response);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean basicTokenAuthentication(String header, HttpServletRequest request, HttpServletResponse response) {
        final String encodedCredentials = header.split(" ")[1];
        try {
            String[] credentials = new String(Base64.getDecoder().decode(encodedCredentials)).split(":");
            if (credentials.length != 2)
                return false;

            String username = credentials[0].trim();
            String token = credentials[1].trim();

            Optional<ExpirationToken> expirationToken = userService.getExpirationToken(token);
            if (!expirationToken.isPresent() || !expirationToken.get().getUser().getEmail().equals(username))
                return false;
            if (expirationToken.get().getExpiration().isBefore(LocalDateTime.now())) {
                userService.deleteToken(token);
                return false;
            }
            userService.validateToken(token);
            User user = expirationToken.get().getUser();
            authWithoutPassword(user);
            response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenUtil.generateAccessToken(user));
            response.setHeader(REFRESH_HEADER, "Bearer " + jwtTokenUtil.generateRefreshToken(user));

            // One time use token
            userService.deleteToken(token);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        } catch (TokenExpiredException e) {
            throw new RuntimeException(e);
        }
    }

    private Locale getCurrentSessionLocale(HttpServletRequest request) {
        if (request != null) {
            return request.getLocale();
        }
        return null;
    }

    private void basicAuthentication(String header, HttpServletRequest request, HttpServletResponse response) {
        String username = "";
        final String encodedCredentials = header.split(" ")[1];
        try {
            String[] credentials = new String(Base64.getDecoder().decode(encodedCredentials)).split(":");
            if (credentials.length != 2)
                return;

            username = credentials[0].trim();
            String password = credentials[1].trim();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, password);

            Authentication authenticate = authenticationManager.authenticate(authentication);

            UserDetails user = (UserDetails) authenticate.getPrincipal();
            User fullUser = userService.getUserByEmail(user.getUsername()).orElse(null);
            if (fullUser == null)
                return;

            response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenUtil.generateAccessToken(fullUser));
            response.setHeader(REFRESH_HEADER, "Bearer " + jwtTokenUtil.generateRefreshToken(fullUser));

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            Locale current = getCurrentSessionLocale(request);
            if (current != fullUser.getLanguage()) {
                userService.changeUserLanguage(fullUser.getId(), current);
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception exception) {
            if (exception instanceof DisabledException) {
                userService.resendToken(username);
            }
        }
    }

    private void authWithoutPassword(User user) {
        Set<RoleType> roles = userService.getUserRoles(user.getId());
        List<GrantedAuthority> authorities = roles.stream().map(roleType -> new SimpleGrantedAuthority("ROLE_" + roleType.getRole())).collect(Collectors.toList());
        Authentication result = new UsernamePasswordAuthenticationToken(new PawAuthUserDetails(user.getEmail(), user.getPassword(), true, true, true, true, authorities), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(result);
    }
}
