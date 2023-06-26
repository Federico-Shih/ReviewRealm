package ar.edu.itba.paw.webapp.config.filters;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationPageFilter extends GenericFilterBean {
    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = ((HttpServletRequest)servletRequest);
        HttpServletResponse response = ((HttpServletResponse) servletResponse);
        if (isAuthenticated() && (request.getRequestURI().equals(request.getContextPath() + "/login") || request.getRequestURI().equals(request.getContextPath() + "/register"))) {
            String encodedredirect = response.encodeRedirectURL(request.getContextPath() + "/");
            response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
            response.setHeader("Location", encodedredirect);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
