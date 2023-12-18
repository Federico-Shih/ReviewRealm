package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.NotificationType;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.NotificationService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.controller.cache.CacheHelper;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.mediatypes.VndType;
import ar.edu.itba.paw.webapp.controller.responses.NotificationResponse;
import ar.edu.itba.paw.webapp.controller.responses.NotificationStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("notifications")
@Component
public class NotificationController {
    @Context
    private UriInfo uriInfo;

    private final MessageSource messageSource;
    private final UserService userService;
    private final NotificationService notificationService;
    private static final int cacheMaxAge = 86400;

    @Autowired
    public NotificationController(NotificationService notificationService, MessageSource messageSource, UserService userService) {
        this.notificationService = notificationService;
        this.messageSource = messageSource;
        this.userService = userService;
    }


    // TODO: Esto está horrible lol, hay que sacarlo (No lo saco porque se van a preguntar dónde está el endpoint)
    @GET
    @Produces(VndType.APPLICATION_NOTIFICATION_LIST)
    public Response getAllNotifications(@Context Request request,
                                        @QueryParam("forUser") Long userId) {
        if(userId != null){
            Optional<User> user = userService.getUserById(userId);
            if(!user.isPresent()){
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok().entity(
                    new GenericEntity<List<NotificationStatusResponse.NotificationTypeResponse>>(
                            NotificationStatusResponse.fromEntity(uriInfo, user.get().getDisabledNotifications())
                    ){}
            ).build();
        }

        List<NotificationType> notificationTypeList = this.notificationService.getNotifications();
        return CacheHelper.conditionalCache(
            Response.ok(
                new GenericEntity<List<NotificationResponse>>(
                    notificationTypeList.stream()
                        .map((notificationType ->
                            NotificationResponse
                                .fromEntity(
                                    uriInfo,
                                    notificationType,
                                    getLocalizedNotification(notificationType)
                                )
                        )).collect(Collectors.toList())) {}
            ),
            request,
            notificationTypeList,
            CacheHelper.buildCacheControl(cacheMaxAge)
        ).build();
    }

    @GET
    @Path("{id}")
    @Produces(VndType.APPLICATION_NOTIFICATION)
    public Response getNotificationById(@PathParam("id") String id, @Context Request request) {
        return this.notificationService.getNotificationTypeById(id)
            .map(notificationType -> CacheHelper.conditionalCache(
                Response.ok(
                    NotificationResponse.fromEntity(
                        uriInfo,
                        notificationType,
                        getLocalizedNotification(notificationType)
                    )
                ),
                request,
                notificationType,
                CacheHelper.buildCacheControl(cacheMaxAge)
            ))
            .orElse(Response.status(Response.Status.NOT_FOUND)).build();
    }

    private String getLocalizedNotification(NotificationType notification) {
        return messageSource.getMessage(notification.getNameCode(), null, LocaleHelper.getLocale());
    }
}
