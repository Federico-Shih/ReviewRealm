package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.JwtTokenUtil;
import ar.edu.itba.paw.webapp.controller.responses.AuthResponse;
import ar.edu.itba.paw.webapp.forms.AuthForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/sessions")
@Component
public class SessionController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid @BeanParam AuthForm authForm) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            authForm.getEmail(),
            authForm.getPassword()
        ));
        User user = (User) auth.getPrincipal();
        String token = jwtTokenUtil.generateAccessToken(user);
        return Response.ok(new AuthResponse(token)).build();
    }
}
