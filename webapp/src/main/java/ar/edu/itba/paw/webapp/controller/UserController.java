package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.MissionProgress;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.MissionService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import ar.edu.itba.paw.webapp.controller.forms.*;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.mediatypes.VndType;
import ar.edu.itba.paw.webapp.controller.querycontainers.UserSearchQuery;
import ar.edu.itba.paw.webapp.controller.responses.*;
import ar.edu.itba.paw.webapp.exceptions.CustomRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("users")
@Component
public class UserController {
    private final UserService us;
    private final MissionService missionService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public UserController(UserService us, MissionService missionService) {
        this.us = us;
        this.missionService = missionService;
    }

    @GET
    @Produces(VndType.APPLICATION_USER_LIST)
    public Response getUsers(@Valid @BeanParam UserSearchQuery userSearchQuery) {
        User loggedIn = AuthenticationHelper.getLoggedUser(us);
        Paginated<User> users;
        try {
            users = us.getUsers(userSearchQuery.getPage(), userSearchQuery.getFilter(), userSearchQuery.getOrdering(), (loggedIn != null) ? loggedIn.getId() : null);
        }catch(UserNotFoundException e){
            return Response.noContent().build();
        }
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
        final User user = us.createUser(registerForm.getUsername(), registerForm.getEmail(), registerForm.getPassword(), LocaleHelper.getLocale());
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path("/users").path(user.getId().toString()).build())
                .entity(UserResponse.fromEntity(uriInfo, user))
                .build();
    }

    @POST
    @Consumes(VndType.APPLICATION_PASSWORD_RESET)
    public Response changePasswordRequest(@Valid @NotNull(message = "error.body.empty") ResendEmailForm emailForm) {
        us.sendPasswordResetToken(emailForm.getEmail());
        return Response.accepted().build();
    }

    @POST
    @Consumes(VndType.APPLICATION_ENABLE_USER)
    public Response validateUser(@Valid @NotNull(message = "error.body.empty") ResendEmailForm emailForm) throws UserAlreadyEnabled {
        us.resendToken(emailForm.getEmail());
        return Response.accepted().build();
    }

    @POST
    @Path("{id:\\d+}/following")
    @Consumes(VndType.APPLICATION_FOLLOWING_FORM)
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    public Response createFollowing(@PathParam("id") long id, @Valid @NotNull(message = "error.body.empty") FollowingForm following) {
        final User user = us.followUserById(id, following.getUserId());
        return Response.noContent().build();
    }

    @DELETE
    @Path("{id:\\d+}/following/{followingId:\\d+}")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    public Response unfollowUser(@PathParam("id") long id,@PathParam("followingId") long followingId) {
        User user = us.unfollowUserById(id,followingId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    @PATCH
    @Path("{id:\\d+}")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    @Consumes(VndType.APPLICATION_PATCH_USER_FORM)
    public Response patchUser(@Valid @NotNull(message = "error.body.empty") PatchUserForm patchUserForm, @PathParam("id") long id) throws InvalidAvatarException {
        us.patchUser(id, patchUserForm.getPassword());
        if (patchUserForm.getGenres() != null) {
           us.setPreferences(patchUserForm.getGenres(), id);
        }
        if (patchUserForm.getAvatarId() != null) {
            us.changeUserAvatar(id, patchUserForm.getAvatarId());
        }
        return Response.noContent().build();
    }

    @POST
    @Path("{id:\\d+}/favoritegames")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    @Consumes(VndType.APPLICATION_FAVORITE_GAME_FORM)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewFavoriteGame(@PathParam("id") final long id, @Valid @NotNull(message = "error.body.empty") FavoriteGameForm favoriteGameForm) {
        boolean added = us.addFavoriteGame(id, favoriteGameForm.getGameId());
        if (!added) throw new CustomRuntimeException(Response.Status.CONFLICT, "error.game.already.favorite");
        return Response.noContent().build();
    }

    @DELETE
    @Path("{id:\\d+}/favoritegames/{gameId:\\d+}")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    public Response deleteFavoriteGame(@PathParam("id") final long id, @PathParam("gameId") final long gameId) {
        boolean deleted = us.deleteFavoriteGame(id, gameId);
        return deleted ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id:\\d+}/notifications")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    @Consumes(VndType.APPLICATION_NOTIFICATIONS_FORM)
    @Produces(VndType.APPLICATION_NOTIFICATION_STATUS)
    public Response putNotifications(@PathParam("id") final long id, @Valid @NotNull(message = "error.body.empty") ChangeNotificationForm changeNotificationForm) {
        User user = us.setUserNotificationSettings(id, changeNotificationForm.getNotificationMap());
        return Response.ok().entity(NotificationStatusResponse.fromEntity(uriInfo, user.getId(), user.getDisabledNotifications())).build();
    }

    @GET
    @Path("{id:\\d+}/notifications")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    @Produces(VndType.APPLICATION_NOTIFICATION_STATUS)
    public Response getNotifications(@PathParam("id") final long id) {
        Optional<User> user = us.getUserById(id);
        if(!user.isPresent()){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().entity(
                new GenericEntity<List<NotificationStatusResponse.NotificationTypeResponse>>(
                        NotificationStatusResponse.fromEntity(uriInfo, user.get().getDisabledNotifications())
                ){}
        ).build();
    }


    @GET
    @Path("{id:\\d+}/mission-progresses")
    @PreAuthorize("@accessControl.checkAccessedUserIdIsUser(#id)")
    @Produces(VndType.APPLICATION_MISSION_PROGRESS)
    public Response getMissionProgresses(@PathParam("id") final long id) {
        List<MissionProgress> progresses = missionService.getMissionProgresses(id);
        if (progresses.isEmpty()) {
            return Response.noContent().build();
        }
        return Response.ok().entity(
                new GenericEntity<List<MissionProgressResponse>>(
                        progresses.stream()
                                .map((mission) -> MissionProgressResponse.fromEntity(uriInfo, mission))
                                .collect(Collectors.toList())
                ){}
        ).build();
    }
}