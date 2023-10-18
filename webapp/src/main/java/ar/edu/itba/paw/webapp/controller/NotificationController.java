package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.NotificationType;
import ar.edu.itba.paw.servicesinterfaces.NotificationService;
import ar.edu.itba.paw.webapp.controller.cache.CacheHelper;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.responses.NotificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;

@Path("/notifications")
@Component
public class NotificationController {
    @Context
    private UriInfo uriInfo;

    private final MessageSource messageSource;

    private final NotificationService notificationService;
    private static final int cacheMaxAge = 86400;

    @Autowired
    public NotificationController(NotificationService notificationService, MessageSource messageSource) {
        this.notificationService = notificationService;
        this.messageSource = messageSource;
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllNotifications(@Context Request request) {
        List<NotificationType> notificationTypeList = this.notificationService.getNotifications();
        return CacheHelper.buildEtagCache(
                request,
                notificationTypeList,
                CacheHelper.buildCacheControl(cacheMaxAge),
                entity -> Response.ok(
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
                )
        ).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotificationById(@PathParam("id") String id, @Context Request request) {
        return this.notificationService.getNotificationTypeById(id)
                .map(notificationType -> CacheHelper.buildEtagCache(
                        request,
                        notificationType,
                        CacheHelper.buildCacheControl(cacheMaxAge),
                        entity -> Response.ok(
                                NotificationResponse.fromEntity(
                                        uriInfo,
                                        entity,
                                        getLocalizedNotification(entity)
                                )
                        )
                ))
                .orElse(Response.status(Response.Status.NOT_FOUND)).build();
    }

    private String getLocalizedNotification(NotificationType notification) {
        return messageSource.getMessage(notification.getNameCode(), null, LocaleHelper.getLocale());
    }
}
