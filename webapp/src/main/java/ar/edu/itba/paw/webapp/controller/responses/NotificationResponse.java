package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.enums.NotificationType;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class NotificationResponse extends BaseResponse {
    private static final String BASE_PATH = "/notifications";
    private final String id;
    private final String localized;


    public NotificationResponse(String id, String localized) {
        this.id = id;
        this.localized = localized;
    }

    public static NotificationResponse fromEntity(final UriInfo uri, NotificationType notificationType, String localized) {
        NotificationResponse response = new NotificationResponse(notificationType.getTypeName(), localized);
        response.link("self", getLinkFromEntity(uri, notificationType));
        return response;
    }

    public static URI getLinkFromEntity(final UriInfo uri, NotificationType notificationType) {
        return uri.getBaseUriBuilder().path(BASE_PATH).path(String.valueOf(notificationType.getTypeName())).build();
    }

    public String getId() {
        return id;
    }

    public String getLocalized() {
        return localized;
    }
}
