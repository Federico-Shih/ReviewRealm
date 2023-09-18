package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.JwtTokenUtil;
import ar.edu.itba.paw.webapp.controller.responses.AuthResponse;
import ar.edu.itba.paw.webapp.controller.forms.AuthForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
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

    @Autowired
    private UserService userService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid @RequestBody AuthForm authForm) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            authForm.getEmail(),
            authForm.getPassword()
        ));
        User user = (User) auth.getPrincipal();

        String token = jwtTokenUtil.generateAccessToken(userService.getUserByEmail(user.getUsername()).orElseThrow(UserNotFoundException::new));
        return Response.ok(new AuthResponse(token)).build();
    }
}
