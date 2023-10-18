package ar.edu.itba.paw.servicesinterfaces;

import ar.edu.itba.paw.enums.NotificationType;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    Optional<NotificationType> getNotificationTypeById(String id);
    List<NotificationType> getNotifications();
}
