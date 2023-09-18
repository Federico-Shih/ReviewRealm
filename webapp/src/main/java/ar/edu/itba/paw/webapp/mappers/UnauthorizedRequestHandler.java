package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

public class UnauthorizedRequestHandler implements AuthenticationEntryPoint {
    private MessageSource messageSource;
    public UnauthorizedRequestHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON);
        response.getWriter().write(String.format("{\n \"message\": \"%s\"\n}", messageSource.getMessage("authenticationexception", null, LocaleHelper.getLocale())));
    }
}