package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.exceptions.EmailAlreadyExistsException;
import ar.edu.itba.paw.exceptions.UserAlreadyEnabled;
import ar.edu.itba.paw.exceptions.UsernameAlreadyExistsException;
import ar.edu.itba.paw.models.MissionProgress;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.MissionService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AccessControl;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentUserId;
import ar.edu.itba.paw.webapp.controller.forms.*;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.mediatypes.VndType;
import ar.edu.itba.paw.webapp.controller.querycontainers.UserSearchQuery;
import ar.edu.itba.paw.webapp.controller.responses.*;
import ar.edu.itba.paw.webapp.exceptions.CustomRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/users")
@Component
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService us;
    private final MissionService missionService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    private AccessControl accessControl;

    @Autowired
    public UserController(UserService us, MissionService missionService) {
        this.us = us;
        this.missionService = missionService;
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@Valid @BeanParam UserSearchQuery userSearchQuery) {
        final Paginated<User> users = us.getUsers(userSearchQuery.getPage(), userSearchQuery.getFilter(), userSearchQuery.getOrdering());
        if (users.getTotalPages() == 0 || users.getList().isEmpty()) {
            return Response.noContent().build();
        }
        List<UserResponse> userResponseList = users.getList().stream().map((user) -> UserResponse.fromEntity(uriInfo, user)).collect(Collectors.toList());
        Response.ResponseBuilder response = Response.ok(PaginatedResponse.fromPaginated(uriInfo, userResponseList, users));
        return response.build();
    }

    @POST
    @Consumes(VndType.APPLICATION_USER)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@Valid RegisterForm registerForm) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        final User user = us.createUser(registerForm.getUsername(), registerForm.getEmail(), registerForm.getPassword(), LocaleHelper.getLocale());
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path("/users").path(user.getId().toString()).build())
                .entity(UserResponse.fromEntity(uriInfo, user))
                .build();
    }

    @POST
    @Consumes(VndType.APPLICATION_PASSWORD_RESET)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePasswordRequest(@Valid ResendEmailForm emailForm) {
        us.sendPasswordResetToken(emailForm.getEmail());
        return Response.accepted().build();
    }

    @POST
    @Consumes(VndType.APPLICATION_ENABLE_USER)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateUser(@Valid ResendEmailForm emailForm) throws UserAlreadyEnabled {
        us.resendToken(emailForm.getEmail());
        return Response.noContent().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/followers")
    public Response getFollowers(@PathParam("id") final long id, @QueryParam("page") @DefaultValue("1") final Integer page, @QueryParam("pageSize") @DefaultValue("10") final Integer pageSize) {
        Paginated<User> followers = us.getFollowers(id, Page.with(page != null ? page : 1, pageSize != null ? pageSize : 10));
        if (followers.getList().isEmpty())
            return Response.noContent().build();
        return Response.ok(
                PaginatedResponse
                        .fromPaginated(uriInfo,
                                followers.getList().stream()
                                        .map((user) -> UserResponse.getLinkFromEntity(uriInfo, user).toString())
                                        .collect(Collectors.toList()),
                                followers)
            ).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/following")
    public Response getFollowing(
            @Valid @ExistentUserId @PathParam("id") final long id,
            @QueryParam("page") @DefaultValue("1") final Integer page,
            @QueryParam("pageSize") @DefaultValue("10") final Integer pageSize
    ) {
        Paginated<User> following = us.getFollowing(id, Page.with(page != null ? page : 1, pageSize != null ? pageSize : 10));
        if (following.getList().isEmpty()) {
            return Response.noContent().build();
        }
        return Response.ok(
                PaginatedResponse
                        .fromPaginated(uriInfo,
                                following.getList().stream()
                                        .map((user) -> UserResponse.getLinkFromEntity(uriInfo, user).toString())
                                        .collect(Collectors.toList()),
                                following)
        ).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/following")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    public Response createFollowing(@PathParam("id") String id, @Valid FollowingForm following) {
        final User user = us.followUserById(Long.parseLong(id), following.getUserId());
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path("/users").path(user.getId().toString()).path("/following").path(following.getUserId().toString()).build())
                .build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("{id}/following/{followingId}")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    public Response unfollowUser(@PathParam("id") String id, @PathParam("followingId") String followingId) {
        User user = us.unfollowUserById(Long.parseLong(id), Long.parseLong(followingId));
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    @PUT
    @Path("{id: \\d+}/preferences")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    public Response putPreferences(@ExistentUserId @PathParam(("id")) Long id, @Valid EditPreferencesForm preferencesForm) {
        us.setPreferences(new HashSet<>(preferencesForm.getGenres()), id);
        return Response.ok().build();
    }

    @GET
    @Path("{id:\\d+}/preferences")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPreferences(@PathParam(("id")) Integer id) {
        final Optional<User> user = us.getUserById(id);
        if (!user.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(ListResponse.fromEntity(uriInfo, user.get().getPreferences().stream().map((genre) -> GenreResponse.getLinkFromEntity(uriInfo, genre)).collect(Collectors.toList())))
                .build();
    }

    @PATCH
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    @Path("{id:\\d+}")
    public Response patchUser(@Valid PatchUserForm patchUserForm, @PathParam("id") Integer id) {
        us.patchUser(id, patchUserForm.getPassword(), patchUserForm.getEnabled());
        return Response.noContent().build();
    }

    @GET
    @Path("{id:\\d+}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getById(@PathParam("id") final long id) {
        final Optional<User> user = us.getUserById(id);
        if (!user.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(UserResponse.fromEntity(uriInfo, user.get())).build();
    }

    @POST
    @Path("{id:\\d+}/favoritegames")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response createNewFavoriteGame(@PathParam("id") final long id, @Valid FavoriteGameForm favoriteGameForm) {
        boolean added = us.addFavoriteGame(id, favoriteGameForm.getGameId());
        if (!added) throw new CustomRuntimeException(Response.Status.BAD_REQUEST, "error.game.already.favorite");
        return Response.created(
                uriInfo.getBaseUriBuilder()
                        .path("users")
                        .path(String.valueOf(id))
                        .path("favoritegames")
                        .path(String.valueOf(favoriteGameForm.getGameId()))
                        .build()
        ).build();
    }

    @DELETE
    @Path("{id:\\d+}/favoritegames/{gameId:\\d+}")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response deleteFavoriteGame(@PathParam("id") final long id, @PathParam("gameId") final long gameId) {
        boolean deleted = us.deleteFavoriteGame(id, gameId);
        return deleted ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id:\\d+}/notifications")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response putNotifications(@PathParam("id") final long id, @Valid ChangeNotificationForm changeNotificationForm) {
        User user = us.setUserNotificationSettings(id, changeNotificationForm.getNotificationMap());
        return Response.ok().entity(NotificationStatusResponse.fromEntity(uriInfo, user)).build();
    }

    @GET
    @Path("{id:\\d+}/notifications")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getNotifications(@PathParam("id") final long id) {
        Optional<User> user = us.getUserById(id);
        if (!user.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().entity(NotificationStatusResponse.fromEntity(uriInfo, user.get())).build();
    }

    @GET
    @Path("{id:\\d+}/mission-progresses")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getMissionProgresses(@PathParam("id") final Long id) {
        List<MissionProgress> progresses = missionService.getMissionProgresses(id);
        if (progresses.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(ListResponse.fromEntity(uriInfo, progresses.stream().map((progress) -> MissionProgressResponse.fromEntity(uriInfo, progress)).collect(Collectors.toList()))).build();
    }

//    @GET
//    @Path("{id:\\d+}/notifications")
//    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
//    @Produces(value = {MediaType.APPLICATION_JSON})
//    public Response getNotifications() {
//
//    }

//    @RequestMapping("/login")
//    public ModelAndView loginForm(S
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