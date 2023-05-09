package ar.edu.itba.paw.models;

import ar.edu.itba.paw.enums.NotificationType;

public class DisabledNotification {
    private final NotificationType notificationType;

    public DisabledNotification(String notificationType) {
        this.notificationType = NotificationType.getByTypeName(notificationType).orElseThrow(IllegalArgumentException::new);
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }
}
