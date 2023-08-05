package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.PawAuthUserDetails;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.querycontainers.UserSearchQuery;
import ar.edu.itba.paw.webapp.controller.responses.PaginatedResponse;
import ar.edu.itba.paw.webapp.controller.responses.UserResponse;
import ar.edu.itba.paw.webapp.forms.ChangePasswordForm;
import ar.edu.itba.paw.webapp.forms.EnableUserForm;
import ar.edu.itba.paw.webapp.forms.RegisterForm;
import ar.edu.itba.paw.webapp.forms.ResendEmailForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/users")
@Component
public class UserController extends UriInfoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService us;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public UserController(UserService us) {
        this.us = us;
    }

    /*
        PUT /users/following/{id}
        DELETE /users/following/{id}
        GET /users/{id}/preferences
        PUT /users/{id}/preferences
        GET /users/{id}/avatar
        PUT /users/{id}/avatar
        GET /users/{id}/favoritegames
        PUT /users/{id}/favoritegames/{gameid}
        DELETE /users/{id}/favoritegames/{gameid}
        GET /users/{id}/notifications
        PUT /users/{id}/notifications/{notificationId}
        DELETE /users/{id}/notifications/{notificationId}
        GET /users/{id}/missions
     */

    @GET
    @Path("{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getById(@PathParam("id") final long id) {
        final Optional<User> user = us.getUserById(id);
        if (!user.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(UserResponse.fromEntity(uriInfo, user.get())).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@Valid @BeanParam UserSearchQuery userSearchQuery) {
        final Paginated<User> users = us.getUsers(userSearchQuery.getPage(), userSearchQuery.getFilter(), userSearchQuery.getOrdering());
        List<UserResponse> userResponseList = users.getList().stream().map(this.currifyUriInfo(UserResponse::fromEntity)).collect(Collectors.toList());
        Response.ResponseBuilder response = Response.ok(PaginatedResponse.fromPaginated(uriInfo, userResponseList, users));
        return response.build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@Valid @BeanParam RegisterForm registerForm) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        final User user = us.createUser(registerForm.getUsername(), registerForm.getEmail(), registerForm.getPassword(), LocaleHelper.getLocale());
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path("/users").path(user.getId().toString()).build())
                .entity(UserResponse.fromEntity(uriInfo, user))
                .build();
    }

    @PUT
    @Path("password")
    public Response changePasswordRequest(@Valid @BeanParam ResendEmailForm emailForm) {
        us.sendPasswordResetToken(emailForm.getEmail());
        return Response.accepted().build();
    }

    @POST
    @Path("password")
    public Response changePassword(@Valid @BeanParam ChangePasswordForm changePasswordForm) throws TokenExpiredException, TokenNotFoundException {
        us.resetPassword(changePasswordForm.getToken(), changePasswordForm.getPassword());
        return Response.noContent().build();
    }

    @PUT
    @Path("enabled")
    public Response validateUserRequest(@Valid @BeanParam ResendEmailForm emailForm) throws UserAlreadyEnabled {
        us.resendToken(emailForm.getEmail());
        return Response.accepted().build();
    }

    @POST
    @Path("enabled")
    public Response validateUser(@Valid @BeanParam EnableUserForm userForm) throws TokenExpiredException {
        us.validateToken(userForm.getToken());
        return Response.noContent().build();
    }

    // TODO: paginate
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/followers")
    public Response getFollowers(@PathParam("id") final long id) {
        List<User> followers = us.getFollowers(id);
        return Response.ok(new GenericEntity<List<UserResponse>>(followers.stream().map(this.currifyUriInfo(UserResponse::fromEntity)).collect(Collectors.toList())){}).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/following")
    public Response getFollowing(@PathParam("id") final long id) {
        List<User> followers = us.getFollowing(id);
        return Response.ok(new GenericEntity<List<UserResponse>>(followers.stream().map(this.currifyUriInfo(UserResponse::fromEntity)).collect(Collectors.toList())){}).build();
    }

//    @RequestMapping("/login")
//    public ModelAndView loginForm(
//            @RequestParam(value = "error", defaultValue = "false") final String error,
//            @RequestParam(value = "disabled", defaultValue = "false") final Boolean disabled
//    ) {
//        ModelAndView mav = new ModelAndView("user/login");
//        mav.addObject("error", error);
//        mav.addObject("disabled", disabled);
//        return mav;
//    }
//
//    @RequestMapping("/register")
//    public ModelAndView registerForm(
//            @ModelAttribute("registerForm") final RegisterForm form
//    ) {
//        return new ModelAndView("user/register");
//    }
//
//    @RequestMapping(value="/register",method = RequestMethod.POST)
//    public ModelAndView register(
//            @Valid @ModelAttribute("registerForm") final RegisterForm form,
//            final BindingResult errors
//    ) {
//        if(errors.hasErrors()) {
//            return registerForm(form);
//        }
//        try {
//            us.createUser(form.getUsername(), form.getEmail(), form.getPassword(), httpServletRequest.getLocale());
//        } catch (EmailAlreadyExistsException | UsernameAlreadyExistsException e) {
//            return registerForm(form);
//        }
//
//        return new ModelAndView("redirect:/recover?registered=true");
//    }
//
//    @RequestMapping(value = "/recover", method = RequestMethod.GET)
//    public ModelAndView validateForm(
//            @RequestParam(value = "registered", required = false) boolean registered,
//            @RequestParam(value = "resent", required = false) boolean resent
//    ) {
//
//        ModelAndView mav = new ModelAndView("user/recovery");
//        mav.addObject("registered", registered);
//        mav.addObject("resent", resent);
//        return mav;
//    }
//
//    @RequestMapping(value = "/resend-email", method = RequestMethod.GET)
//    public ModelAndView resendEmailForm(
//            @ModelAttribute("resendEmailForm") ResendEmailForm form,
//            @RequestParam(value = "email", required = false) String email
//    ) {
//        if (email != null && !email.equals("")) {
//            form.setEmail(email);
//        }
//        return new ModelAndView("user/resend-email");
//    }
//
//    @RequestMapping(value = "/validate", method = RequestMethod.POST)
//    public ModelAndView validateToken(@RequestParam("validationCode") String code) {
//        Optional<User> user;
//        try {
//            user = us.validateToken(code);
//        } catch (TokenExpiredException e) {
//            LOGGER.error("Token expired " + code);
//            return new ModelAndView("user/recovery").addObject("expiredToken", true);
//        }
//        if (!user.isPresent()) {
//            return new ModelAndView("user/recovery").addObject("unknownToken", true);
//        }
//        authWithoutPassword(user.get());
//        return new ModelAndView("user/validated");
//    }
//
//    private void authWithoutPassword(User user) {
//         Set<RoleType> roles = us.getUserRoles(user.getId());
//         List<GrantedAuthority> authorities = roles.stream().map(roleType -> new SimpleGrantedAuthority("ROLE_" + roleType.getRole())).collect(Collectors.toList());
//         Authentication result = new UsernamePasswordAuthenticationToken(new PawAuthUserDetails(user.getEmail(), user.getPassword(), authorities), null, authorities);
//         SecurityContextHolder.getContext().setAuthentication(result);
//    }
//
//    @RequestMapping(value = "/validate/{token}", method = RequestMethod.GET)
//    public ModelAndView validateByPath(@PathVariable("token") String token) {
//        return validateToken(token);
//    }
//
//    @RequestMapping(value = "/resend-email", method = RequestMethod.POST)
//    public ModelAndView resendEmail(@Valid @ModelAttribute("resendEmailForm") ResendEmailForm form, final BindingResult errors) {
//        if (errors.hasErrors()) {
//            return resendEmailForm(form, form.getEmail());
//        }
//        try {
//            us.resendToken(form.getEmail());
//        } catch (UserAlreadyEnabled e) {
//            errors.rejectValue("email", "user.already.exists");
//            return resendEmailForm(form, form.getEmail());
//        }
//        return new ModelAndView("redirect:/recover?resent=true");
//    }
//
//    @RequestMapping(value = "/changepassword", method = RequestMethod.GET)
//    public ModelAndView changePasswordForm(@RequestParam(value = "token", required = true) String token,
//                                           @ModelAttribute("passwordForm") ChangePasswordForm passwordForm) {
//        ModelAndView mav = new ModelAndView("user/change-password");
//        mav.addObject("token", token);
//        return mav;
//    }
//
//    @RequestMapping(value = "/recover-password", method = RequestMethod.GET)
//    public ModelAndView recoverPasswordForm(@ModelAttribute("emailForm") ResendEmailForm emailForm) {
//        return new ModelAndView("user/resend-recover");
//    }
//
//    @RequestMapping(value = "/resend-password", method = RequestMethod.POST)
//    public ModelAndView sendChangePasswordRequest(@Valid @ModelAttribute("emailForm") ResendEmailForm emailForm,
//                                                  final BindingResult errors) {
//        if (errors.hasErrors()) {
//            return recoverPasswordForm(emailForm);
//        }
//        us.sendPasswordResetToken(emailForm.getEmail());
//        return new ModelAndView("user/resend-recover").addObject("resent", true);
//    }
//
//    @RequestMapping(value = "/changepassword/{token}", method = RequestMethod.POST)
//    public ModelAndView changePassword(@PathVariable("token") String token,
//                                       @Valid @ModelAttribute("passwordForm") ChangePasswordForm passwordForm,
//                                       final BindingResult errors) {
//        if (errors.hasErrors()) {
//            return changePasswordForm(token, passwordForm);
//        }
//        try {
//            us.resetPassword(token, passwordForm.getPassword());
//        } catch (TokenExpiredException e) {
//            LOGGER.error("Token expired " + token);
//            errors.rejectValue("repeatPassword", "error.token.expired");
//            return changePasswordForm(token, passwordForm);
//        } catch (TokenNotFoundException e) {
//            errors.rejectValue("repeatPassword", "error.token.not.found");
//            return changePasswordForm(token, passwordForm);
//        }
//        return new ModelAndView("user/password-changed");
//    }

}

/*
POST /recovertoken email
PUT /users/1/password
POST /validatetoken email
PUT /users/1/enabled ???
POST /users
PUT /users/1
PUT /users/1/preferences
 */