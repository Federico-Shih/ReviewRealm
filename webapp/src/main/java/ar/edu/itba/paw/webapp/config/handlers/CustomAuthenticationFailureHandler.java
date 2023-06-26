package ar.edu.itba.paw.webapp.config.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        if (e instanceof DisabledException) {
            response.sendRedirect(String.format("%s/login?disabled=true", request.getContextPath()));
        } else {
            response.sendRedirect(String.format("%s/login?error=true", request.getContextPath()));
        }
    }
}
