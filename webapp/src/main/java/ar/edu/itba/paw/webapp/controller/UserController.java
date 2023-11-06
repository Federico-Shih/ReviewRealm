package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.enums.NotificationType;
import ar.edu.itba.paw.exceptions.EmailAlreadyExistsException;
import ar.edu.itba.paw.exceptions.UserAlreadyEnabled;
import ar.edu.itba.paw.exceptions.UsernameAlreadyExistsException;
import ar.edu.itba.paw.models.MissionProgress;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.MissionService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AccessControl;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentGameId;
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
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/api/users")
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

    @GET
    @Produces(VndType.APPLICATION_USER_LIST)
    public Response getUsers(@Valid @BeanParam UserSearchQuery userSearchQuery) {
        User loggedIn = AuthenticationHelper.getLoggedUser(us);
        final Paginated<User> users = us.getUsers(userSearchQuery.getPage(), userSearchQuery.getFilter(), userSearchQuery.getOrdering(), (loggedIn != null) ? loggedIn.getId() : null);
        if (users.getTotalPages() == 0 || users.getList().isEmpty()) {
            return Response.noContent().build();
        }
        List<UserResponse> userResponseList = users.getList().stream().map(
                (user) -> UserResponse.UserResponseBuilder
                        .fromUser(user, uriInfo)
                        .withAuthed(loggedIn)
                        .build()
        ).collect(Collectors.toList());
        Response.ResponseBuilder response = PaginatedResponseHelper.fromPaginated(uriInfo, userResponseList, users);
        return response.build();
    }

    @GET
    @Path("{id:\\d+}")
    @Produces(VndType.APPLICATION_USER)
    public Response getById(@PathParam("id") final long id) {
        User loggedIn = AuthenticationHelper.getLoggedUser(us);
        final Optional<User> user = us.getUserById(id, loggedIn != null ? loggedIn.getId() : null);
        if (!user.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(
                UserResponse.UserResponseBuilder
                        .fromUser(user.get(), uriInfo)
                        .withAuthed(loggedIn)
                        .build()
        ).build();
    }

    @POST
    @Consumes(VndType.APPLICATION_USER_FORM)
    @Produces(VndType.APPLICATION_USER)
    public Response createUser(@Valid @NotNull(message = "error.body.empty") RegisterForm registerForm) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        LOGGER.info("Creating user with username " + registerForm.getUsername() + " and email " + registerForm.getEmail());
        final User user = us.createUser(registerForm.getUsername(), registerForm.getEmail(), registerForm.getPassword(), LocaleHelper.getLocale());
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path("/users").path(user.getId().toString()).build())
                .entity(UserResponse.fromEntity(uriInfo, user))
                .build();
    }

    @POST
    @Consumes(VndType.APPLICATION_PASSWORD_RESET)
    public Response changePasswordRequest(@Valid @NotNull(message = "error.body.empty") ResendEmailForm emailForm) {
        LOGGER.info("User with email " + emailForm.getEmail() + " requested password reset");
        us.sendPasswordResetToken(emailForm.getEmail());
        return Response.accepted().build();
    }

    @POST
    @Consumes(VndType.APPLICATION_ENABLE_USER)
    public Response validateUser(@Valid @NotNull(message = "error.body.empty") ResendEmailForm emailForm) throws UserAlreadyEnabled {
        us.resendToken(emailForm.getEmail());
        return Response.ok().build();
    }

    @GET
    @Produces(VndType.APPLICATION_ENTITY_LINK_LIST)
    @Path("{id:\\d+}/followers")
    public Response getFollowers(@PathParam("id") final long id, @QueryParam("page") @DefaultValue("1") final Integer page, @QueryParam("pageSize") @DefaultValue("10") final Integer pageSize) {
        Paginated<User> followers = us.getFollowers(id, Page.with(page != null ? page : 1, pageSize != null ? pageSize : 10));
        if (followers.getList().isEmpty())
            return Response.noContent().build();
        return PaginatedResponseHelper.fromPaginated(uriInfo,
                followers.getList().stream()
                        .map((user) -> UserResponse.getLinkFromEntity(uriInfo, user).toString())
                        .collect(Collectors.toList()),
                followers).build();
    }

    @GET
    @Produces(VndType.APPLICATION_ENTITY_LINK_LIST)
    @Path("{id:\\d+}/following")
    public Response getFollowing(
            @Valid @ExistentUserId @PathParam("id") final long id,
            @QueryParam("page") @DefaultValue("1") final Integer page,
            @QueryParam("pageSize") @DefaultValue("10") final Integer pageSize
    ) {
        Paginated<User> following = us.getFollowing(id, Page.with(page != null ? page : 1, pageSize != null ? pageSize : 10));
        if (following.getList().isEmpty()) {
            return Response.noContent().build();
        }
        return PaginatedResponseHelper
                .fromPaginated(uriInfo,
                        following.getList().stream()
                                .map((user) -> UserResponse.getLinkFromEntity(uriInfo, user).toString())
                                .collect(Collectors.toList()),
                        following).build();
    }

    @POST
    @Path("{id:\\d+}/following")
    @Consumes(VndType.APPLICATION_FOLLOWING_FORM)
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    public Response createFollowing(@PathParam("id") String id, @Valid @NotNull(message = "error.body.empty") FollowingForm following) {
        final User user = us.followUserById(Long.parseLong(id), following.getUserId());
        return Response
                .created(uriInfo.getBaseUriBuilder().path("/users").path(user.getId().toString()).path("/following").path(following.getUserId().toString()).build())
                .build();
    }

    @DELETE
    @Path("{id:\\d+}/following/{followingId:\\d+}")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    public Response unfollowUser(@PathParam("id") String id, @PathParam("followingId") String followingId) {
        User user = us.unfollowUserById(Long.parseLong(id), Long.parseLong(followingId));
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    @PATCH
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    @Path("{id:\\d+}")
    @Produces(VndType.APPLICATION_PATCH_USER_FORM)
    public Response patchUser(@Valid @NotNull(message = "error.body.empty") PatchUserForm patchUserForm, @ExistentUserId @PathParam("id") Long id) {
        us.patchUser(id, patchUserForm.getPassword(), patchUserForm.getEnabled());
        if (patchUserForm.getGenres() != null) {
            us.setPreferences(patchUserForm.getGenres(), id);
        }
        return Response.noContent().build();
    }

    @POST
    @Path("{id:\\d+}/favoritegames")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    @Consumes(VndType.APPLICATION_FAVORITE_GAME_FORM)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewFavoriteGame(@ExistentUserId @PathParam("id") final long id, @Valid @NotNull(message = "error.body.empty") FavoriteGameForm favoriteGameForm) {
        boolean added = us.addFavoriteGame(id, favoriteGameForm.getGameId());
        if (!added) throw new CustomRuntimeException(Response.Status.BAD_REQUEST, "error.game.already.favorite");
        return Response.created(
                uriInfo.getBaseUriBuilder()
                        .path("users") //TODO: check url and maybe put /games?favoriteOf=id
                        .path(String.valueOf(id))
                        .path("favoritegames")
                        .path(String.valueOf(favoriteGameForm.getGameId()))
                        .build()
        ).build();
    }

    @DELETE
    @Path("{id:\\d+}/favoritegames/{gameId:\\d+}")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    public Response deleteFavoriteGame(@ExistentUserId @PathParam("id") final long id, @ExistentGameId @PathParam("gameId") final long gameId) {
        boolean deleted = us.deleteFavoriteGame(id, gameId);
        return deleted ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id:\\d+}/notifications")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    @Consumes(VndType.APPLICATION_NOTIFICATIONS_FORM)
    @Produces(VndType.APPLICATION_NOTIFICATION_STATUS)
    public Response putNotifications(@ExistentUserId @PathParam("id") final long id, @Valid @NotNull(message = "error.body.empty") ChangeNotificationForm changeNotificationForm) {
        User user = us.setUserNotificationSettings(id, changeNotificationForm.getNotificationMap());
        return Response.ok().entity(NotificationStatusResponse.fromEntity(uriInfo, user.getId(), user.getDisabledNotifications())).build();
    }

    @GET
    @Path("{id:\\d+}/notifications")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    @Produces(VndType.APPLICATION_NOTIFICATION_STATUS)
    public Response getNotifications(@PathParam("id") final long id) {
        Set<NotificationType> notifications = us.getNotifications(id).orElseThrow(() -> new CustomRuntimeException(Response.Status.NOT_FOUND, "user.not.found"));
        return Response.ok().entity(NotificationStatusResponse.fromEntity(uriInfo, id, notifications)).build();
    }

    @GET
    @Path("{id:\\d+}/mission-progresses")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    @Produces(VndType.APPLICATION_MISSION_PROGRESS)
    public Response getMissionProgresses(@PathParam("id") final Long id) {
        List<MissionProgress> progresses = missionService.getMissionProgresses(id);
        if (progresses.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(ListResponse.fromEntity(uriInfo, progresses.stream().map((progress) -> MissionProgressResponse.fromEntity(uriInfo, progress)).collect(Collectors.toList()))).build();
    }
}