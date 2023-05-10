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

    @Override
    public int hashCode() {
        return notificationType.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DisabledNotification))
            return false;
        DisabledNotification disabledNotification = (DisabledNotification) obj;
        return disabledNotification.notificationType.equals(this.notificationType);
    }
}
