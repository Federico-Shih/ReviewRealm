package ar.edu.itba.paw.models;

import ar.edu.itba.paw.converters.NotificationTypeAttributeConverter;
import ar.edu.itba.paw.enums.NotificationType;

import javax.persistence.*;

@Entity
@Table(name = "notifications")
public class DisabledNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notifications_notificationid_seq")
    @SequenceGenerator(sequenceName = "notifications_notificationid_seq", name = "notifications_notificationid_seq", allocationSize = 1)
    @Column(name = "notificationid")
    private Long notificationid;

    @Column(name = "notificationtype", nullable = false)
    @Convert(converter = NotificationTypeAttributeConverter.class)
    private NotificationType notificationType;

    public DisabledNotification(String notificationType) {
        this.notificationType = NotificationType.getByTypeName(notificationType).orElseThrow(IllegalArgumentException::new);
    }

    protected DisabledNotification() {
        // Just for Hibernate
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

    public Long getNotificationid() {
        return notificationid;
    }
}
